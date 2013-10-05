package functions;

//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.util.ArrayList;

public class ColorDetector {
	
	private IplImage imgThreshold;
	private IplImage sourceImage;
	
	// halogen-ish
	//private static int GOLDCUTOFF = 50;
	// fluro-ish
	private static int GOLDCUTOFF = 50;
	
	public ColorDetector(IplImage sourceImage) {
		this.sourceImage = sourceImage;
	}
  
	public void hsvThresholdCoord(int x, int y) {
	  // 8-bit, 3- color =(RGB)
		IplImage imgHSV = cvCreateImage(cvGetSize(sourceImage), 8, 3);
		cvCvtColor(sourceImage, imgHSV, CV_BGR2HSV);
      
		// uses bottom pixel x,y as hue calibration
		
		// GOOD FOR TABLE RECOGNITION
		//Double range = 5.0;
		Double range = 5.0;
		
		ArrayList<Double> hsvValues = getPixelColor(imgHSV, x, y);
		Double hueLower = hsvValues.get(0)-range;
		Double hueUpper = hsvValues.get(0)+range;
		
		// 8-bit 1- color = monochrome
		imgThreshold = cvCreateImage(cvGetSize(sourceImage), 8, 1);
		// cvScalar : ( H , S , V, A)
		
		// GOOD FOR TABLE RECOGNITION
		cvInRangeS(imgHSV, cvScalar(hueLower, 100, 50, 0), cvScalar(hueUpper, 255, 255, 0), imgThreshold);
		
		cvReleaseImage(imgHSV);
		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
      
		//return imgThreshold.clone();
	}
	
	public void hsvThresholdGold() {
		  // 8-bit, 3- color =(RGB)
			IplImage imgHSV = cvCreateImage(cvGetSize(sourceImage), 8, 3);
			cvCvtColor(sourceImage, imgHSV, CV_BGR2HSV);
			
			// 8-bit 1- color = monochrome
			imgThreshold = cvCreateImage(cvGetSize(sourceImage), 8, 1);
			// cvScalar : ( H , S , V, A)

			cvInRangeS(imgHSV, cvScalar(5, 50, 0, 0), cvScalar(GOLDCUTOFF, 255, 200, 0), imgThreshold);
			
			cvReleaseImage(imgHSV);
			cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
			//cvSaveImage("gold_threshold.png", imgThreshold);
		}
	
	public void hsvThresholdSilver() {
		  // 8-bit, 3- color =(RGB)
			IplImage imgHSV = cvCreateImage(cvGetSize(sourceImage), 8, 3);
			cvCvtColor(sourceImage, imgHSV, CV_BGR2HSV);
			
			// 8-bit 1- color = monochrome
			imgThreshold = cvCreateImage(cvGetSize(sourceImage), 8, 1);
			// cvScalar : ( H , S , V, A)

			cvInRangeS(imgHSV, cvScalar(0, 0, 0, 0), cvScalar(255, 50, 200, 0), imgThreshold);
			
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