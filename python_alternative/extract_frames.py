import cv2
from detectron2.config import get_cfg
from detectron2.engine import DefaultPredictor
from detectron2 import model_zoo
from detectron2.data import MetadataCatalog
import torch
import ffmpeg
import shutil
import os


class extract_frames:
    @staticmethod
    def check_rotation(path_video_file):
        # this returns meta-data of the video file in form of a dictionary

        meta_dict = ffmpeg.probe(path_video_file)

        # from the dictionary, meta_dict['streams'][0]['tags']['rotate'] is the key
        # we are looking for
        rotateCode = None
        if (
            meta_dict.get("streams", [dict(tags=dict())])[0]
            .get("tags", dict())
            .get("rotate", 0)
            == "90"
        ):
            rotateCode = cv2.ROTATE_180
        elif (
            meta_dict.get("streams", [dict(tags=dict())])[0]
            .get("tags", dict())
            .get("rotate", 0)
            == "180"
        ):
            rotateCode = cv2.ROTATE_180
        elif (
            meta_dict.get("streams", [dict(tags=dict())])[0]
            .get("tags", dict())
            .get("rotate", 0)
            == "270"
        ):
            rotateCode = cv2.ROTATE_180

        return rotateCode

    @staticmethod
    def correct_rotation(frame, rotateCode):
        return cv2.rotate(frame, rotateCode)

    @staticmethod
    def process(video_name):
        print("setting up model and predictor to use...\n")
        cfg = get_cfg()
        cfg.merge_from_file(
            model_zoo.get_config_file(
                "COCO-InstanceSegmentation/mask_rcnn_R_50_DC5_1x.yaml"
            )
        )
        cfg.MODEL.ROI_HEADS.SCORE_THRESH_TEST = 0.7  # set threshold for this model
        cfg.MODEL.DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
        cfg.MODEL.WEIGHTS = model_zoo.get_checkpoint_url(
            "COCO-InstanceSegmentation/mask_rcnn_R_50_DC5_1x.yaml"
        )
        predictor = DefaultPredictor(cfg)
        class_names = MetadataCatalog.get(cfg.DATASETS.TRAIN[0]).thing_classes
        pred_class_names = dict(zip(class_names, range(len(class_names))))
        objects_to_filter = [
            pred_class_names["person"],
            pred_class_names["skateboard"],
            pred_class_names["bicycle"],
            pred_class_names["backpack"],
        ]

        cap = cv2.VideoCapture("videos/" + video_name)
        rotateCode = extract_frames.check_rotation("videos/" + video_name)
        total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        current_frame = 0
        width = int(cap.get(3))
        height = int(cap.get(4))
        fps = cap.get(5)
        print(
            f"image stats:\nname: {video_name}, width: {width}, height: {height}, fps: {fps}\n"
        )

        print("cleaning folders...\n")
        result_folder_name = f"result/{video_name}/"
        shutil.rmtree(result_folder_name, ignore_errors=True)
        os.mkdir(result_folder_name)
        background_result_folder_name = result_folder_name + "background_frames/"
        foreground_result_folder_name = result_folder_name + "foreground_frames/"
        os.mkdir(background_result_folder_name)
        os.mkdir(foreground_result_folder_name)

        fourcc = cv2.VideoWriter_fourcc("V", "P", "8", "0")
        foreground = cv2.VideoWriter(
            result_folder_name + "foreground.webm", fourcc, fps, (width, height)
        )

        i = 0
        success, frame = cap.read()

        while success:
            if rotateCode is not None:
                frame = extract_frames.correct_rotation(frame, rotateCode)
            instances = predictor(frame)["instances"]
            filtered_object_mask = sum(
                instances.pred_classes == o for o in objects_to_filter
            ).bool()
            masks = instances[filtered_object_mask].pred_masks
            masked = frame.copy()
            background = frame.copy()
            all_mask = (
                torch.zeros((height, width)).bool().to(torch.device(cfg.MODEL.DEVICE))
            )
            for mask in masks:
                all_mask = torch.logical_or(all_mask, mask)
            cpu_mask = all_mask.cpu()
            masked[cpu_mask == False] = [255, 255, 255]
            # background[cpu_mask == True] = [0, 0, 0]
            background = cv2.inpaint(
                background,
                cpu_mask.to(torch.uint8).unsqueeze(-1).numpy(),
                3,
                cv2.INPAINT_TELEA,
            )

            foreground.write(masked)
            cv2.imwrite(f"{foreground_result_folder_name}{i:03d}.jpg", masked)
            cv2.imwrite(f"{background_result_folder_name}{i:03d}.jpg", background)
            i += 1
            print(f"at frame {i}/{total_frames}")
            success, frame = cap.read()

        foreground.release()
        cap.release()


if __name__ == "__main__":
    for video in os.listdir("videos"):
        if video.endswith(".mp4"):
            extract_frames.process(video)
