package capture;

import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * 
 * @author Benjamin Rose & Ben Merange
 *
 * This Class is utilized to visualize pixel HSV values by printing
 * the current values at the mouse position to the console.
 * 
 */

public class OverlayImage extends IplImage {

	   public OverlayImage(IplImage one, IplImage two, double level) {
	    	cvAddWeighted(one, 1.0, two, level, 0.0, this);
	    }

	   public OverlayImage(IplImage one, IplImage two) {
	    	cvAddWeighted(one, 1.0, two, 0.5, 0.0, this);
	    }
	
}
