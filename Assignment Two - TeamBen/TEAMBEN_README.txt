
METR4202 - Team Delta - Team Ben (#19)
================================
Coin Sensing and Perception Project.

Team Members: Ben Merange, Ben Rose

This project was written using eclipse Build 20130919-0819 and JavaSE 1.7

This project uses the following external libraries/packages:
-Kinect SDK 1.8
-OpenNI2.2.30b
-JavaCV 0.6b
-OpenCV 2.4.6.0
-J3D 1.5.2
-NyARToolkit

ALL FUNCTIONALITY IS ACCESSIBLE FROM THE MAIN GUI (CoinGUI)
Color and camera calibration are not necessary (but may improve results).

Run in order of:
	1) Snap color image
	2) Find axis
	3) Rectify image
	4) Find coins


Setup to run:
1. Install Kinect SDK 1.8 (make sure there are no other kinect drivers installed)
2. 


The following functionality has been implemented:

-----------------------------------------------------------
  Color Calibration (colorCalibration Package)
-----------------------------------------------------------

Color calibration is achieved through the use of a Macbeth color checker.
The ColorChart class handles the main processes by recognising the chart
within an image and finding the RGB, HSV and YCrCb values corresponding to
gold and silver.

-----------------------------------------------------------
  Camera Calibration (cameraCalibration Package)
-----------------------------------------------------------

Camera calibration is achieved using the OpenCV (via JavaCV bindings) camera
calibration methods and a checkerboard of user-settable size. This class is
implemented to support a large number of samples and is capable of autonomous
calibration.

The calibration results can be easily applied to a captured image
(button included in the user interface) and will be printed in the
interface console.

-----------------------------------------------------------
  Coin Detection   (functions Package)
-----------------------------------------------------------

The CoinFinder class interfaces with other classes in this package.
PixelColorVisualiser is not used directly, but is handy for debugging.

The process of coin detection is achieved in a number of steps:
1.	Image rectification is achieved within the ImageRectifier class by
	sampling and linearising a consistent section of depth data (such as a
	table or other flat surface) and using OpenCV's warpPerspective
	functionality to bring the surface to a face-on view. A rotation matrix
	is also stored for later use.
2. 	Colors are thresholded to give areas of gold and silver.
3.	Circles are identified within the thresholded image. If there is
	a plate, data outside the plate area will be excluded.
4.	The identified circles are transformed back into the original image.
	The approximate depth data at each coin allows calculation of the 
	vector to each coin in physical mm from an origin placed at the camera.
5.	The transformation matrix supplied from the localisation package, the
	rotation from the rectification process and the previously calculated
	vector are then used to calculate the location of each coin relative
	to the placed reference frame.

-----------------------------------------------------------
  Localisation  (localisation Package)
-----------------------------------------------------------

The reference axis is identified within an image within this package.
The resulting transformation matrix from this process is utilised within the
functions package (CoinFinder class) to locate the coins.

Axislocator looks at an image and returns the location and orientation of
a predefined fiducial using NyARToolkit 

-----------------------------------------------------------
  Image Capture  (capture Package)
-----------------------------------------------------------

This package includes a class for reading kinect depth and low and high
definition color images. The kinectreader uses OpenNI 2.x and kinect SDK 1.7+.

*Future Implementation: support for any webcam for image capture*
