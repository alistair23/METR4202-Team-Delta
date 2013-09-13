package functions;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.ArrayList;
 
public class HoughCircles{
	
	private IplImage sourceImage;
	private ArrayList<Float> circleData;
	private ArrayList<Integer> radiusData;
	
	public HoughCircles(IplImage sourceImage) {
		this.sourceImage = sourceImage;
		circleData = new ArrayList<Float>();
		radiusData = new ArrayList<Integer>();
	}

	public void runHoughCirclesRGBPlate() {

		IplImage gray = cvCreateImage(cvGetSize(sourceImage), 8, 1);
   
		cvCvtColor(sourceImage, gray, CV_BGR2GRAY);
		cvSmooth(gray, gray, CV_GAUSSIAN, 3);
   
		CvMemStorage mem = CvMemStorage.create();
   
		CvSeq circles = cvHoughCircles( 
				gray, //Input image
				mem, //Memory Storage
				CV_HOUGH_GRADIENT, //Detection method
				1, //Inverse ratio
				1000, //Minimum distance between the centers of the detected circles
				20, //Higher threshold for canny edge detector
				50, //Threshold at the center detection stage
				100, //min radius
				1000 //max radius
				);
   
		for(int i = 0; i < circles.total(); i++){
			CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
			int radius = Math.round(circle.z());      
			cvCircle(sourceImage, center, radius, CvScalar.GREEN, 1, CV_AA, 0);
			
			circleData.add(circle.x()); circleData.add(circle.y());
			radiusData.add(radius);
		}
	}
	
	public void runHoughCirclesRGBCoins() {

		IplImage gray = cvCreateImage(cvGetSize(sourceImage), 8, 1);
   
		cvCvtColor(sourceImage, gray, CV_BGR2GRAY);
		cvSmooth(gray, gray, CV_GAUSSIAN, 3);
   
		CvMemStorage mem = CvMemStorage.create();
   
		CvSeq circles = cvHoughCircles( 
				gray, //Input image
				mem, //Memory Storage
				CV_HOUGH_GRADIENT, //Detection method
				1, //Inverse ratio
				10, //Minimum distance between the centers of the detected circles
				20, //Higher threshold for canny edge detector
				50, //Threshold at the center detection stage
				10, //min radius
				100 //max radius
				);
   
		for(int i = 0; i < circles.total(); i++){
			CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
			int radius = Math.round(circle.z());      
			cvCircle(sourceImage, center, radius, CvScalar.GREEN, 1, CV_AA, 0);
			
			circleData.add(circle.x()); circleData.add(circle.y());
			radiusData.add(radius);
		}
	}
	
	public void runHoughCirclesHSV() {

		IplImage gray = cvCreateImage(cvGetSize(sourceImage), 8, 1);
		
		cvCvtColor(sourceImage, gray, CV_BGR2GRAY);
		cvSmooth(gray, gray, CV_GAUSSIAN, 3);
   
		CvMemStorage mem = CvMemStorage.create();
   
		CvSeq circles = cvHoughCircles( 
				gray, //Input image
				mem, //Memory Storage
				CV_HOUGH_GRADIENT, //Detection method
				1, //Inverse ratio
				10, //Minimum distance between the centers of the detected circles
				20, //Higher threshold for canny edge detector
				50, //Threshold at the center detection stage
				10, //min radius
				100 //max radius
				);
   
		for(int i = 0; i < circles.total(); i++){
			CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
			int radius = Math.round(circle.z());      
			cvCircle(sourceImage, center, radius, CvScalar.RED, 1, CV_AA, 0);
			
			circleData.add(circle.x()); circleData.add(circle.y());
		}
	}
	
	public void display(String title) {
		cvShowImage(title,sourceImage);  
		cvWaitKey(0);
	}
	
	// returns the image with circle data overlayed
	public IplImage getImage() {
		return sourceImage.clone();
	}
	
	// returns an array list of the x,y coordinates of circle centers
	public ArrayList<Float> getCircleDataList() {
		return (ArrayList<Float>) circleData.clone();
	}
	
	public ArrayList<Integer> getRadiusDataList() {
		return (ArrayList<Integer>) radiusData.clone();
	}
}