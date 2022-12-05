import cv2
import os


class stitcher:
    @staticmethod
    def stitch(video_name, key_frame_interval=5, confidence_threshold=1.0):
        print(f"video name {video_name}")
        result_folder_name = f"result/{video_name}"
        background_frames_folder_name = f"{result_folder_name}/background_frames"
        images = os.listdir(background_frames_folder_name)
        images = sorted(images)
        backgrounds = []
        for i in range(len(images)):
            # if it's a key frame, push it to the list of backgrounds
            if i % key_frame_interval == 0 or i == len(images):
                backgrounds.append(
                    cv2.imread(f"{background_frames_folder_name}/{images[i]}")
                )
                print(f"read frame {i}")

        stitcher = cv2.Stitcher_create(mode=cv2.STITCHER_PANORAMA)
        stitcher.setPanoConfidenceThresh(confidence_threshold)

        # stitch the backgrounds
        print(f"\nstitching {len(backgrounds)} background images...")
        result = stitcher.stitch(backgrounds)
        if result[0] != 0:
            print("failed")
        else:
            cv2.imwrite(result_folder_name + "/pano.jpg", result[1])


if __name__ == "__main__":
    stitcher.stitch("SAL.mp4", key_frame_interval=5,
                    confidence_threshold=1.0)
