package cameraCalibration;

import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_ADAPTIVE_THRESH;
import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_NORMALIZE_IMAGE;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvCalibrateCamera2;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvFindChessboardCorners;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvDrawChessboardCorners;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseMat;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvInitUndistortMap;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvRemap;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import com.googlecode.javacv.cpp.opencv_core.CvMat;

/**
 * 
 * @author Benjamin Rose & Ben Merange
 *
 * This class is used to handle the calibration of camera intrinsics and extrinsics
 * from a number of input IplImages of 5 by 4 chessboards.
 *
 */

public class CameraCalibrator {

	 CvPoint2D32f corners; // stores the position of the checkerboard corners in pixels
	 public CvSize boardSize = new CvSize(5,4);
	 int pointNumber = boardSize.width()*boardSize.height();
	 
	 public int Samples = 10;
	 public int SampleAt = 0;
	 
	 public	 CvMat objectPoints = CvMat.create(pointNumber*Samples,3);
	 public	 CvMat imagePoints = CvMat.create(pointNumber*Samples,2);
	 
	public double fx; //focal points
	public double fy; 
	public double cx; //principat point coordinates
	public double cy;
	
	public double k1; //coeffs of radial distortion
	public double k2;
	public double k3;
	public double p1; //coeffs of tangential distortion
	public double p2;
	
	
	public	 CvMat cameraMatrix = CvMat.create(3,3);	
	public	 CvMat distCoeffs = CvMat.create(5,1);
	public	 CvMat rotVectors = CvMat.create(Samples,3);
	public	 CvMat transVectors = CvMat.create(Samples,3);
			 
	public	 CvSize Resolution = new CvSize(640, 480);
	public	 CvMat mapx = CvMat.create(Resolution.height(), Resolution.width(), CV_32FC1);
	public	 CvMat mapy = CvMat.create(Resolution.height(), Resolution.width(), CV_32FC1);

	public Double error;
	public int patternFound;
		
	public CameraCalibrator(){
		setup();
	}

	// builds up coordimnate system of the grid
	
	public void setup(){
		//set up object points
		 objectPoints = CvMat.create(pointNumber*Samples,3);
		 imagePoints = CvMat.create(pointNumber*Samples,2);
		 rotVectors = CvMat.create(Samples,3);
		 transVectors = CvMat.create(Samples,3);

		
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
			patternFound = 0;

		    // Find chessboard corners
		    corners = new CvPoint2D32f(boardSize.width() * boardSize.height());
		    int[] cornerCount = new int[1];
		    int flags = CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE;
		    patternFound = cvFindChessboardCorners(image, boardSize, corners, cornerCount, flags);

		    IplImage image2 = image.clone();
		    
		    // Draw the corners
		    cvDrawChessboardCorners(image2, boardSize, corners, cornerCount[0], patternFound);
		    
		    return image2;
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
		} else {
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
		double error = cvCalibrateCamera2(objectPoints,imagePoints,	pointCount,	Resolution,cameraMatrix, distCoeffs, rotVectors, transVectors, 0);
		cvReleaseMat(pointCount);
		
		cvInitUndistortMap(cameraMatrix, distCoeffs, mapx, mapy);
		this.error = error;
		 fx = cameraMatrix.get(0,0); //focal points
		 fy = cameraMatrix.get(1,1); 
		 cx = cameraMatrix.get(0,2); //principat point coordinates
		 cy = cameraMatrix.get(1,2);
		
		 k1 = distCoeffs.get(0); //coeffs of radial distortion
		 k2 = distCoeffs.get(1);
		 k3 = distCoeffs.get(4);
		 p1 = distCoeffs.get(2); //coeffs of tangential distortion
		 p2 = distCoeffs.get(3);
		
		return error;
	}
	
	public IplImage remap(final IplImage image){
		cvInitUndistortMap(cameraMatrix, distCoeffs, mapx, mapy);

		IplImage undistortedImage = image.clone();
		cvRemap(undistortedImage, undistortedImage, mapx, mapy, CV_INTER_LINEAR, CvScalar.ZERO);
		return undistortedImage;
	}
	
	public IplImage remap2(final IplImage image){
		IplImage undistortedImage = image.clone();
		cvRemap(image, undistortedImage, mapx, mapy, CV_INTER_LINEAR, CvScalar.ZERO);
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


