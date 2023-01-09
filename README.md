# Generating novel videos assisted by Background-  Foreground Panorama Analysis!

Given an input video, where the camera is panning in a local area, eg a sports event, the project involves an analysis of the video frames to separate all the objects in motion from the background. With this  separation, the foreground elements can be extracted on a frame-by-frame basis. After the extraction a composite of all the background areas of all the frames can be registered together to create a large panorama.


# Input

The input to your process will be a video (mp4 or rgb sequence) having the following naming convention  
videoname_width_height_numofframes. Parsing the name should give you everything about the video if  
you choose to use the rgb sequence. You may not need this parsing step if you are reading directly from  
an mp4 reader. Here are a few instances.  
GeneratePanorame.exe inputvideo_640_480_290.rgb  
GeneratePanorame.exe inputvideo_640_480_290.mp4

# Intermediary Outputs<img width="927" alt="Screenshot 2023-01-08 at 8 09 15 PM" src="https://user-images.githubusercontent.com/20672326/211239787-bb2da187-ada7-45df-8a43-7325ecdfd3d8.png">


1. A generated panorama of the background with foreground elements removed.
2. Foreground macroblock elements or objects with coordinates on a frame-by-frame basis.

# Application Outputs:

1. Display motion trails by compositing every nth foreground element on the panorama, where n is  your choice of a number. The motion trail is a nice image showing a map of where the foreground  objects were in the panorama.<img width="806" alt="Screenshot 2023-01-08 at 8 09 51 PM" src="https://user-images.githubusercontent.com/20672326/211239833-13c3568e-e15c-471e-8d77-872bc843fd8a.png">

2. Create a video a  new video  by defining a path in the panorama image, but it should overlap with the foreground objects in some capacity with the expectation that the foreground objects are composited in time synchronized manner<img width="761" alt="Screenshot 2023-01-08 at 8 10 16 PM" src="https://user-images.githubusercontent.com/20672326/211239869-2d4e51c3-20b8-48c3-8025-184a2ea1d387.png">

3. Remove objects from video: When your video is processed, you have a background panorama and you have one (or more) foreground objects
<img width="731" alt="Screenshot 2023-01-08 at 8 10 44 PM" src="https://user-images.githubusercontent.com/20672326/211239884-3e7fc8b7-9b9b-4295-ba76-cd99125a5223.png">


# Algorithm:
## Step 1: Detecting foreground and background macroblocks using motion compensation  
You are required to divide each frame of the video into  background  and  foreground  macroblocks objects. Note that your macroblocks may not be precise, especially at the boundaries but nevertheless you should be able to segment out contiguous blocks of foreground elements and separate the background.  
Enumerated below are some useful guidelines:  
1.  Divide each image frame into blocks of size 16x16 pixels  
2.  Compute the Motion Vectors based on the previous frame – this is the block-based MAD 
3.  Organize the blocks into background and foreground based on the similarity of the directions of their motion vectors. Background macro blocks either have a close to zero motion vectors (if camera is not moving) or a constant same motion vector (when the camera is moving). On the  
other hand, foreground macroblocks have similar motion vectors with macroblocks of a moving  
region and are directionally different from background motion vectors. 
## Step 2: Creating a panorama for the background  
Next we need to choose an “anchor” frame to initialize your panorama. The background panorama is created by warping the neighborhood frames around the anchor frame and compositing the warped “missing” content at each step. This amounts to computing a  transform  from every frame to see how it fits into the panorama image.  
1.  If the camera has moved horizontally or vertically, this transform may be approximated  
as a translation matrix. This may be true for some of the given datasets.  
2.  If the camera has rotated about is pivot, this transform may be approximated as a rotation  
matrix. This may be true for some of the given datasets.  
3.  In general, given the various degrees of freedom (T, R, S and perspective changes), this  
transform is best approximated as a 3x3 perspective transform (homography)
## Detectron2 
is Facebook AI Research's next generation library that provides state-of-the-art detection and segmentation algorithms. It is the successor of [Detectron](https://github.com/facebookresearch/Detectron/) and [maskrcnn-benchmark](https://github.com/facebookresearch/maskrcnn-benchmark/). It supports a number of computer vision research projects and production applications in Facebook. We use this library for foreground detection
##  SIFT and RANSAC
Scale-Invariant Feature Transform (SIFT)—SIFT is an algorithm in computer vision to detect and describe local features in images. It is a feature that is widely used in image processing. The processes of SIFT include Difference of Gaussians (DoG) Space Generation, Keypoints Detection, and Feature Description. Interest points of an image are located using SIFT of python openCV package and those are matched between two images using FLANN based matcher. Ransac algorithm is used to maximize the number of inliers and DLT (Direct Linear Transform) is used to compute Homography.
