package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;

import java.awt.Point;
import java.util.ArrayList;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;

public class FindCorners {
	
	private IplImage image;

	public FindCorners(IplImage image) {
    	this.image = image;
	}
	
	public void hmm() {
	    // Find chessboard corners
	    //CvSize patternSize = new CvSize(6, 4);
		CvSize patternSize = new CvSize(8,8);
	    CvPoint2D32f corners = new CvPoint2D32f(patternSize.width() * patternSize.height());
	    //int[] cornerCount = Array(1);
	    int[] cornerCount = { 0 };
	    int flags = CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE;
	    int patternFound = cvFindChessboardCorners(image, patternSize, corners, cornerCount, flags);
	    
	    System.out.println(String
				.format("found = %s, corner_count = %s", patternFound,
						cornerCount[0]));
	    
	    // Draw the corners
	    cvDrawChessboardCorners(image, patternSize, corners, cornerCount[0], patternFound);
	    cvShowImage("Corners on Chessboard", image);  
		cvWaitKey(0);
	}
	
	public void findObject() {
		IplImage baseImage = cvLoadImage("test_images/color_checker.png");
		ObjectFinder finder = new ObjectFinder(image);
		double[] dat = finder.find(baseImage);
		System.out.println(dat);
		
		for (int i=0; i < dat.length; i+=2) {
			CvPoint pt = new CvPoint((int)dat[i], (int)dat[i+1]);
			cvCircle(image, pt, 3, CvScalar.GREEN, 1, CV_AA, 0);
		}
		cvShowImage("Corners", image);  
		cvWaitKey(0);
		//rectangle(image, new Point((int)dat[0], (int)dat[1]), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	}
}