package trials;

//imports
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.util.ArrayList;

public class ColorDetector {
	
  public static void main(String[] args) {
      IplImage orgImg = cvLoadImage("img1.png");
      //cvSaveImage("hsvthreshold.jpg", hsvThreshold(orgImg));
      cvShowImage("Result",hsvThreshold(orgImg));  
      cvWaitKey(0);
  }
  
  static IplImage hsvThreshold(IplImage orgImg) {
      // 8-bit, 3- color =(RGB)
      IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
      System.out.println(cvGetSize(orgImg));
      cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
      
      // uses bottom pixel 0,0 as hue calibration
      Double range = 5.0;
      ArrayList<Double> hsvValues = getPixelColor(imgHSV, 0, 0);
      Double hueLower = hsvValues.get(0)-range;
      Double hueUpper = hsvValues.get(0)+range;
      
      // 8-bit 1- color = monochrome
      IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
      // cvScalar : ( H , S , V, A)
      cvInRangeS(imgHSV, cvScalar(hueLower, 100, 50, 0), cvScalar(hueUpper, 255, 255, 0), imgThreshold);
      cvReleaseImage(imgHSV);
      cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
      
      return imgThreshold;
  }
  
  public static ArrayList<Double> getPixelColor(IplImage hsvImage, int x, int y) {
	  CvScalar s=cvGet2D(hsvImage, y, x);                
      System.out.println( "H:"+ s.val(0) + " S:" + s.val(1) + " V:" + s.val(2));//Print values
      
      ArrayList<Double> values = new ArrayList<Double>();
      values.add(s.val(0)); values.add(s.val(1)); values.add(s.val(2));
      return values;
	}
  
}