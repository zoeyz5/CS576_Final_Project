import cv2
import ffmpeg


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


def correct_rotation(frame, rotateCode):
    return cv2.rotate(frame, rotateCode)


video_name = "video2.mp4"
cap = cv2.VideoCapture("videos/" + video_name)
rotateCode = check_rotation(video_name)

i = 0
success, frame = cap.read()
while success:
    if rotateCode is not None:
        frame = correct_rotation(frame, rotateCode)
    cv2.imwrite(f"frames/{video_name}/{i:03d}.jpg", frame)
    i += 1
    print(f"at frame {i}")
    success, frame = cap.read()
