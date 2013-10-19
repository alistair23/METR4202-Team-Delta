package functions;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.util.ArrayList;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class is mainly used to threshold an IplImage to around
 * standard Australian currency gold and silver.
 * 
 * Standard thresholding:
 * Either hsvThresholdGold() or hsvThresholdSilver().
 * 
 * getThresholdImage() will then return the resulting image.
 * 
 */

public class ColorDetector {
	
	private IplImage imgThreshold;
	private IplImage sourceImage;
	
	// VARY THIS VALUE FOR ANTICIPATED LIGHTING CONDITIONS
	// WHITE: ~50		HALOGEN: ~30
	private static int GOLDCUTOFF = 30;
	
	public ColorDetector(IplImage sourceImage) {
		this.sourceImage = sourceImage;
	}
  
	public void hsvThresholdCoord(int x, int y) {
		IplImage imgHSV = cvCreateImage(cvGetSize(sourceImage), 8, 3);
		cvCvtColor(sourceImage, imgHSV, CV_BGR2HSV);
		Double range = 5.0;
		
		ArrayList<Double> hsvValues = getPixelColor(imgHSV, x, y);
		Double hueLower = hsvValues.get(0)-range;
		Double hueUpper = hsvValues.get(0)+range;
		
		imgThreshold = cvCreateImage(cvGetSize(sourceImage), 8, 1);
		
		cvInRangeS(imgHSV, cvScalar(hueLower, 100, 50, 0), cvScalar(hueUpper, 255, 255, 0), imgThreshold);
		cvReleaseImage(imgHSV);
		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
	}
	
	public void hsvThresholdGold() {
			IplImage imgHSV = cvCreateImage(cvGetSize(sourceImage), 8, 3);
			cvCvtColor(sourceImage, imgHSV, CV_BGR2HSV);
			imgThreshold = cvCreateImage(cvGetSize(sourceImage), 8, 1);
			
			// THESE VALUES CAN BE VARIED TO BE MORE ROBUST TO DIFFERENT LIGHTING CONDITIONS
			cvInRangeS(imgHSV, cvScalar(0, 70, 0, 0), cvScalar(GOLDCUTOFF, 255, 230, 0), imgThreshold);
			cvReleaseImage(imgHSV);
			cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
	}
	
	public void hsvThresholdSilver() {
			IplImage imgHSV = cvCreateImage(cvGetSize(sourceImage), 8, 3);
			cvCvtColor(sourceImage, imgHSV, CV_BGR2HSV);
			imgThreshold = cvCreateImage(cvGetSize(sourceImage), 8, 1);
			
			// THESE VALUES CAN BE VARIED TO BE MORE ROBUST TO DIFFERENT LIGHTING CONDITIONS
			cvInRangeS(imgHSV, cvScalar(0, 0, 0, 0), cvScalar(255, 80, 190, 0), imgThreshold);
			cvReleaseImage(imgHSV);
			cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
	}
  
	private static ArrayList<Double> getPixelColor(IplImage hsvImage, int x, int y) {
		CvScalar s=cvGet2D(hsvImage, y, x);                
		System.out.println( "H:"+ s.val(0) + " S:" + s.val(1) + " V:" + s.val(2));//Print values
		
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(s.val(0)); values.add(s.val(1)); values.add(s.val(2));
		return values;
	}
	
	public void display() {
		cvShowImage("ColorDetector", imgThreshold);  
		cvWaitKey(0);
	}
	
	public IplImage getThresholdImage() {
		return imgThreshold.clone();
	}
}