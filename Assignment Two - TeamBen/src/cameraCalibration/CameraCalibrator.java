package cameraCalibration;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_ADAPTIVE_THRESH;
import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_NORMALIZE_IMAGE;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvFindChessboardCorners;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvDrawChessboardCorners;

import java.io.File;

import capture.KinectReader;
import colorCalibration.FindCorners;

import com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;

public class CameraCalibrator {

	
	public static void main(String[] args) {
		
		KinectReader kr = new KinectReader();
		
		kr.Start();
		
		CameraCalibrator cc = new CameraCalibrator();
		
		IplImage image = kr.getColorFrame();
		//IplImage image = cvLoadImage("test_images/chessboard.jpg");
		
		cc.FindChessboard(image);
	}

	

	public IplImage FindChessboard (IplImage image){
	
	// Read input image
			

		    // Find chessboard corners
		    CvSize patternSize = new CvSize(5, 4);
		    CvPoint2D32f corners = new CvPoint2D32f(patternSize.width() * patternSize.height());
		    int[] cornerCount = new int[1];
		    int flags = CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE;
		    int patternFound = cvFindChessboardCorners(image, patternSize, corners, cornerCount, flags);

		    // Draw the corners
		    cvDrawChessboardCorners(image, patternSize, corners, cornerCount[0], patternFound);
		    //cvShowImage("Corners on Chessboard",image);
		    //cvWaitKey(0);
		    return image;
		}
	}


