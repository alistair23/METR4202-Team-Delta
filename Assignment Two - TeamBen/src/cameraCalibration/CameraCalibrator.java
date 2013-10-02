package cameraCalibration;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_ADAPTIVE_THRESH;
import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_NORMALIZE_IMAGE;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvCalibrateCamera2;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvFindChessboardCorners;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvDrawChessboardCorners;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseMat;

import java.io.File;

import capture.KinectReader;
import colorCalibration.FindCorners;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvInitUndistortMap;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvRemap;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;

/**
 * 
 * @author Benjamin
 *
 *initialise this class
 *
 *this.setup();
 *
 *keep running calibrate(image);
 *
 *
 *
 *
 */


public class CameraCalibrator {

	 CvPoint2D32f corners; // stores the position of the checkerboard corners in pixels
	 CvSize boardSize = new CvSize(7,7);
	 int pointNumber = boardSize.width()*boardSize.height();
	 
	 public int Samples = 13;
	 public int SampleAt = 0;
	 
	 CvMat objectPoints = CvMat.create(pointNumber*Samples,3);
	 CvMat imagePoints = CvMat.create(pointNumber*Samples,2);
	 
	 
	 CvMat cameraMatrix = CvMat.create(3,3);	
	 CvMat distCoeffs = CvMat.create(5,1);
	 CvMat rotVectors = CvMat.create(Samples,3);
	 CvMat transVectors = CvMat.create(Samples,3);
			 
	 CvSize Resolution = new CvSize(640, 480);
	 CvMat mapx = CvMat.create(Resolution.height(), Resolution.width(), CV_32FC1);
	 CvMat mapy = CvMat.create(Resolution.height(), Resolution.width(), CV_32FC1);

	Double error;		
		
	public static void main(String[] args) {
		
		//Setup the kinect reader
		KinectReader kr = new KinectReader();
		kr.Start();
		
		//setup the camera calibrator
		CameraCalibrator cc = new CameraCalibrator();
		cc.setup();
		
		//get a frame and add it to the calibration data until our samples are filled.
		
		for(int i = 0;i<cc.Samples;i++){
		IplImage image = kr.getColorFrame();
		cc.addToCalibration(image);
		}
		
		//perform the calibration on the current samples
		System.out.println(String.valueOf(cc.calibrate()));
		IplImage image = cvLoadImage("test_images/chessboard.jpg");
		
		
		cvShowImage("undistorted", cc.remap(kr.getColorFrame())); 
		cvWaitKey(0);
		
		//cc.FindChessboard(image);
	}

	// builds up coordimnate system of the grid
	
	public void setup(){
		//set up object points
		int idx = 0;
		
		for(int f=0; f<Samples; f++){
			for(int i=boardSize.height()-1; i>=0; i--){
				for(int j=0; j<boardSize.width(); j++){
					//objectPoints.position(f);
					
					objectPoints.put(idx, 0,(double)(j));
					objectPoints.put(idx, 1,(double)(i));
					objectPoints.put(idx, 2,(double)(0));
					idx++;
				}
			}
		}
	}

	public IplImage FindChessboard (IplImage image){
	
	// Read input image
			

		    // Find chessboard corners
		    corners = new CvPoint2D32f(boardSize.width() * boardSize.height());
		    int[] cornerCount = new int[1];
		    int flags = CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE;
		    int patternFound = cvFindChessboardCorners(image, boardSize, corners, cornerCount, flags);

		    // Draw the corners
		    cvDrawChessboardCorners(image, boardSize, corners, cornerCount[0], patternFound);
		    //cvShowImage("Corners on Chessboard",image);
		    //cvWaitKey(0);
		    return image;
		}
	
	public boolean addToCalibration(IplImage image){
		
		corners = new CvPoint2D32f(boardSize.width() * boardSize.height());
	    int[] cornerCount = new int[1];
	    int flags = CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE;
	    int patternFound = cvFindChessboardCorners(image, boardSize, corners, cornerCount, flags);
		
	    if(patternFound > 0){
		
	    FindChessboard(image);
		
		
		//set up imagepoints
		for(int p=0; p<this.pointNumber; p++){
			this.imagePoints.put(SampleAt*pointNumber+p, 0, corners.position(p).x());
			this.imagePoints.put(SampleAt*pointNumber+p, 1, corners.position(p).y());
		}
		
		SampleAt++;
		
		System.out.println(SampleAt);
		
		if(SampleAt > Samples){
			SampleAt = 0;
		}
		}else{
		
		System.out.println("No Board Found!");
		return false;
		}
	    return true;
	}
	
	public double calibrate(){
		CvMat pointCount = cvCreateMat(Samples, 1, CV_32SC1);
		for(int i=0; i<Samples; i++){
			pointCount.put(i,this.pointNumber);
		}
		
		
		System.out.println("Calibrating");
		double error = cvCalibrateCamera2(objectPoints,imagePoints,	pointCount,	Resolution,cameraMatrix, distCoeffs, null, null, 0);
		cvReleaseMat(pointCount);
		cvInitUndistortMap(cameraMatrix, distCoeffs, mapx, mapy);
		this.error = error;
		return error;
	}
	
	public IplImage remap(final IplImage image){
		IplImage undistortedImage = image;
		cvRemap(image, undistortedImage, mapx, mapy, CV_INTER_LINEAR, CvScalar.ZERO);
		cvShowImage("image", image);  
		cvShowImage("undistorted", undistortedImage);  
		cvWaitKey(0);
		return undistortedImage;
	}
	
	public boolean hasBoard(IplImage image){
		boolean d = false;
		
	    corners = new CvPoint2D32f(boardSize.width() * boardSize.height());
	    int[] cornerCount = new int[1];
	    int flags = CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE;
	    int patternFound = cvFindChessboardCorners(image, boardSize, corners, cornerCount, flags);

	    if(patternFound > 0){
	    	d = true;
	    }
	  
		
		return d;
	}
	
	public boolean isCalibrated(){
		boolean r = false;
		if(this.error != null){
			r = true;
		}
		return r;
	}
	
	}


