package cameraCalibration;

	import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;

import capture.KinectReader;

	import com.googlecode.javacpp.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.*;

	import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_video.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;

	public class CameraCalibration4 {
		
		CvMat imagePoints;
		CvMat objectPoints;
		CvMat cameraMatrix;
		CvMat distCoeffs;
		int flag;
		CvMat mapx, mapy;
		boolean mustInitUndistort;
		CvSize boardSize;
		int pointNumber;
		int fileNum;
		CvSize imageSize;
		IplImage[] imgarray = new IplImage[10];
		
		public static void main(String[] args) {
			
			KinectReader kr = new KinectReader();
			
			kr.Start();
			
			CameraCalibration4 cc = new CameraCalibration4(new CvSize(6,4),1,new CvSize(640,480));
			System.out.println(cc.addChessboardPoints());
			System.out.println(cc.calibrate());
			IplImage image = kr.getColorFrame();
			IplImage undistortedImage = kr.getColorFrame();
			cc.remap(image, undistortedImage);
			
		}
		
		public CameraCalibration4(CvSize boardSize, int fileNumber, CvSize imageSize){
			this.imagePoints = CvMat.create(boardSize.width()*boardSize.height()*fileNumber,2);
			this.objectPoints = CvMat.create(boardSize.width()*boardSize.height()*fileNumber,3);
			this.cameraMatrix = CvMat.create(3, 3); 
			this.distCoeffs = CvMat.create(5,1);
			this.flag = 0;
			this.mapx = CvMat.create(imageSize.height(), imageSize.width(), CV_32FC1);
			this.mapy = CvMat.create(imageSize.height(), imageSize.width(), CV_32FC1);
			this.mustInitUndistort = true;
			this.boardSize = boardSize;
			this.pointNumber = boardSize.width()*boardSize.height();
			this.fileNum = fileNumber;
			this.imageSize = imageSize;
		}
		
		public void getImages(){
	  		KinectReader kr = new KinectReader();
			
			kr.Start();
			
			for(int i = 0; i<imgarray.length;i++){
			imgarray[i] = kr.getColorFrame();
			}
		}
		
		public int addChessboardPoints(){
			for(int f=0; f<fileNum; f++){
				for(int i=0; i<boardSize.height(); i++){
					for(int j=0; j<boardSize.width(); j++){
						objectPoints.put((48*f) + (i*boardSize.width()+j), 0, i);
						objectPoints.put((48*f) + (i*boardSize.width()+j), 1, j);
						objectPoints.put((48*f) + (i*boardSize.width()+j), 2, 0);
					}
				}
			}
			
			int successes = 0;
			for(int i=0; i<fileNum; i++){
				//IplImage img = cvLoadImage("calib/"+i+".jpg");
				
				IplImage img = cvLoadImage("test_images/chessboard.jpg");
				
				CvPoint2D32f imageCorners = new CvPoint2D32f(this.pointNumber);
				int found = cvFindChessboardCorners(img, boardSize, imageCorners, null, CV_CALIB_CB_ADAPTIVE_THRESH);
				if(found != 0){
					
					for(int p=0; p<this.pointNumber; p++){
						this.imagePoints.put(i*pointNumber+p, 0, imageCorners.position(p).x());
						this.imagePoints.put(i*pointNumber+p, 1, imageCorners.position(p).y());
					}
					successes++;
				}
				cvReleaseImage(img);
				imageCorners.deallocate();
			}
			
			return successes;
		}
		
		public double calibrate(){
			this.mustInitUndistort = true;
			CvMat pointCount = cvCreateMat(fileNum, 1, CV_32SC1);
			for(int i=0; i<fileNum; i++){
				pointCount.put(i,this.pointNumber);
			}
			double error = cvCalibrateCamera2(objectPoints, imagePoints, pointCount, imageSize,	cameraMatrix, distCoeffs, null, null, flag);
			cvReleaseMat(pointCount);
			return error;
		}
		
		public void remap(IplImage image, IplImage undistortedImage){
			if(mustInitUndistort){
				cvInitUndistortMap(cameraMatrix, distCoeffs, mapx, mapy);
			}
			mustInitUndistort = false;
			cvRemap(image, undistortedImage, mapx, mapy, CV_INTER_LINEAR, CvScalar.ZERO);
			//cvShowImage("image", image);  
			//cvShowImage("undistorted", undistortedImage);  
			//cvWaitKey(0);
		}

	}
