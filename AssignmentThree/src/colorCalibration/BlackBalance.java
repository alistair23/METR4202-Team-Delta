package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class allows for easy HSV value return of the center
 * point of a black image for color calibration purposes.
 *
 */

public class BlackBalance {
	
	private IplImage sourceImage;
	
	public BlackBalance(IplImage sourceImage) {
		this.sourceImage = sourceImage;
	}
	
	public CvScalar getHsvValues() {
		IplImage hsvImage = cvCreateImage(cvGetSize(sourceImage),8,3);
		cvCvtColor(sourceImage, hsvImage, CV_RGB2HSV);
		int x = cvGetSize(sourceImage).width()/2;
		int y = cvGetSize(sourceImage).height()/2;
		CvScalar hsvData = cvGet2D(hsvImage, y, x);
		return hsvData;
	}
}
