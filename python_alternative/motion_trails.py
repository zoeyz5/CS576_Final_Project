import cv2
import os
import numpy as np

VID_NAME = "test2"
VID_PATH = os.path.join(os.getcwd(), "python_alternative", "result", f"{VID_NAME}.mp4")
ORIG_VID = os.path.join(os.getcwd(), "python_alternative", "videos", f"{VID_NAME}.mp4")
BG_PATH = os.path.join(VID_PATH, "background_frames")
FG_PATH = os.path.join(VID_PATH, "foreground_frames")
OG_PATH = os.path.join(VID_PATH, "original_frames")
if not os.path.exists(OG_PATH):
    os.mkdir(OG_PATH)
KEY_FRAMES = 5
PANO_CONFIDENCE_THRESH = .75
#best so far: 0.75, 25

def build_panos():
    # read the background frames
    frames = []
    for i in range(0, len(os.listdir(BG_PATH))):
        if i == 0 or i % KEY_FRAMES == 0 or i == len(os.listdir(BG_PATH)):
            frames.append(cv2.imread(os.path.join(BG_PATH, str(i).zfill(3) + ".jpg")))
    print("Done reading {} background frames".format(len(frames)))

    final_frames = []
    for i in range(0, len(os.listdir(FG_PATH))):
        if i == 0 or i % KEY_FRAMES == 0:
            final_frames.append(cv2.imread(os.path.join(FG_PATH, str(i).zfill(3) + ".jpg")))
    
    print("Computing transform...")
    # stitch the frames together to form the panorama
    stitcher = cv2.Stitcher_create(mode=cv2.Stitcher_PANORAMA)
    #(status, result) = stitcher.stitch(frames)
    stitcher.setPanoConfidenceThresh(PANO_CONFIDENCE_THRESH)
    status = stitcher.estimateTransform(frames)
    if status != cv2.Stitcher_OK:
        print("estimateTransform failed with error code {}".format(status))
        exit(1)
    print("Composing background frames...")
    status, result1 = stitcher.composePanorama(frames)
    if status != cv2.Stitcher_OK:
        print("composeFrames (bg) failed with error code {}".format(status))
        exit(1)
    print("Composing foreground frames...")
    status, result2 = stitcher.composePanorama(final_frames)
    if status != cv2.Stitcher_OK:
        print("composeFrames (fg) failed with error code {}".format(status))
        exit(1)
    
    print("Done stitching...writing to file")
    cv2.imwrite(os.path.join(VID_PATH, "motion_trails.jpg"), result1)
    cv2.imwrite(os.path.join(VID_PATH, "motion_trails2.jpg"), result2)

def get_original_frames():
    vidcap = cv2.VideoCapture(ORIG_VID)
    success, image = vidcap.read()
    print("Reading original frames...")
    count = 0
    while success:
        cv2.imwrite(os.path.join(OG_PATH, str(count).zfill(3) + ".jpg"), image)
        print("Read frame {}".format(count))
        success, image = vidcap.read()
        count += 1

def stitch2(image1, image2):
    imageA = cv2.imread(image1)
    imageB = cv2.imread(image2)
    stitcher = cv2.Stitcher_create()
    status = stitcher.estimateTransform([imageA, imageB])
    if status != cv2.Stitcher_OK:
        print("computeTransform failed with error code {}".format(status))
        exit(1)
    status, result = stitcher.composePanorama([imageA, imageB])
    if status != cv2.Stitcher_OK:
        print("composePanarama failed with error code {}".format(status))
        exit(1)
    return result

def combine_panos(bg, fg):
    LOWER_THRESH = 10
    UPPER_THRESH = 200
    bg = cv2.imread(bg)
    fg = cv2.imread(fg)
    h = fg.shape[0]
    w = fg.shape[1]
    result = bg.copy()
    for i in range(0, h):
        for j in range(0, w):
            # if the foreground pixel is below a threshold value, use the background pixel
            if not (fg[i][j][0] < LOWER_THRESH and fg[i][j][1] < LOWER_THRESH and fg[i][j][2] < LOWER_THRESH) and not (fg[i][j][0] > UPPER_THRESH and fg[i][j][1] > UPPER_THRESH and fg[i][j][2] > UPPER_THRESH):
                result[i][j] = fg[i][j]
    cv2.imwrite(os.path.join(VID_PATH, "motion_trails_final.jpg"), result)

def translate(img, x, y):
    M = np.float32([[1, 0, x], [0, 1, y]])
    shifted = cv2.warpAffine(img, M, (img.shape[1], img.shape[0]))
    cv2.imwrite(os.path.join(VID_PATH, "f2.jpg"), shifted)

if __name__ == "__main__":
    #get_original_frames()
    #build_panos()
    translate(cv2.imread(os.path.join(VID_PATH, "f1.jpg")), 54, 56)
    combine_panos(os.path.join(VID_PATH, "b1.jpg"), os.path.join(VID_PATH, "f2.jpg"))
   