METR4202 - Team Delta - Team Ben
================================
Coin Sensing and Perception Project.

Team Members: Ben Merange, Ben Rose

This project was written using eclipse Build 20130919-0819 and JavaSE 1.7

This project uses the following external libraries/packages:
-Kinect SDK 1.8
-OpenNI2.2.30b
-JavaCV 0.6b
-J3D 1.5.2
-NyARToolkit


The following functionality has been implemented:

-----------------------------------------------------------
  Color Calibration (colorCalibration Package)
-----------------------------------------------------------

Color calibration is achieved through the use of a Macbeth color checker.
The ColorChart class handles the main processes 

-----------------------------------------------------------
  Camera Calibration (cameraCalibration Package)
-----------------------------------------------------------

Camera Calibration is done using the OpenCV (via JavaCV bindings) camera calibration methods and 
a checkerboard of user-settable size. this class is implemented to support 
a large number of samples and is capable of autonomous calibration.

-----------------------------------------------------------
  Coin Detection   (coinDetection Package)
-----------------------------------------------------------

Coin Detection is... ***should be in analysis?**

-----------------------------------------------------------
  Localisation  (localisation Package)
-----------------------------------------------------------

This package contains calsses aimed at localising elements in an image.
Axislocator looks at an image and returns the location and orientation of
a predefined Fiducial using NyARToolkit 
****coinFinder****

-----------------------------------------------------------
  Image Capture  (capture Package)
-----------------------------------------------------------

This package includes a class for reading kinect depth and low and high definition color images.
the kinectreader uses OpenNI 2.x and kinect SDK 1.7+.
*Future Implementation: support for any webcam for image capture*

-----------------------------------------------------------
  Image Manipulation and Analysis   (analysis Package)
-----------------------------------------------------------

this contains classes responsible for analysing and manipulating images.



