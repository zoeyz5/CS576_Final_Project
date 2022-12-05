import cv2
from detectron2.config import get_cfg
from detectron2.engine import DefaultPredictor
from detectron2 import model_zoo

cfg = get_cfg()
cfg.merge_from_file(model_zoo.get_config_file(
    "COCO-InstanceSegmentation/mask_rcnn_R_50_DC5_1x.yaml"))
cfg.MODEL.ROI_HEADS.SCORE_THRESH_TEST = 0.5  # set threshold for this model
# cfg.MODEL.DEVICE = 'cpu'
cfg.MODEL.WEIGHTS = model_zoo.get_checkpoint_url(
    "COCO-InstanceSegmentation/mask_rcnn_R_50_DC5_1x.yaml")
predictor = DefaultPredictor(cfg)

cap = cv2.VideoCapture('videos/test1.mp4')
total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
current_frame = 0
width = int(cap.get(3))
print(width)
height = int(cap.get(4))
print(height)
fps = cap.get(5)
print(fps)
fourcc = cv2.VideoWriter_fourcc('M', 'J', 'P', 'G')
foreground = cv2.VideoWriter('/foreground_vids/test1.mp4', fourcc, fps, (width, height))

stitcher = cv2.Stitcher_create(mode=0)
result = None
backgrounds = []
i = 0
success, frame = cap.read()

while success:
    instances = predictor(frame)['instances']
    masks = instances[instances.pred_classes == 0].pred_masks
    masked = frame.copy()
    background = frame.copy()
    for mask in masks:
        cpu_mask = mask.cpu()
        masked[cpu_mask == False] = [0, 0, 0]
        background[cpu_mask == True] = [0, 0, 0]
    if i == 0:
        backgrounds.append(frame.copy())
    elif i % 10 == 0 or i == total_frames:
        backgrounds.append(background.copy())
    foreground.write(masked)
    i += 1
    print(f"at frame {i}")
    success, frame = cap.read()

backgrounds.reverse()
result = stitcher.stitch(backgrounds)[1]
cv2.imwrite("/panos/test1.jpg", result)
foreground.release()
cap.release()
