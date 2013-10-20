package capture;

import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * 
 * @author Benjamin Rose & Ben Merange
 *
 * This Class is utilized to visualize pixel HSV values by printing
 * the current values at the mouse position to the console.
 * 
 */

public class OverlayImage {
	
		public OverlayImage() {
			
		}
		
	   public IplImage overlay(IplImage one, IplImage two, double level) {
		   	System.out.println();
		   	IplImage overlayed = cvCreateImage(cvGetSize(one), 8, 3);
	    	cvAddWeighted(one, 1.0, two, level, 0.0, overlayed);
	    	return overlayed;
	    }
	
}
