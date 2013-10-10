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

public class OverlayImage implements Runnable {

	KinectReader kr;
	public IplImage overlayed;
	private double level = 0.0;
	
    public OverlayImage(KinectReader kr, double level) {
        this.kr = kr;
        this.level = level;
    }
	
	@Override
	public void run() {
		overlayed = kr.getDepthFrame();
		IplImage depthframe = kr.getDepthFrame();
		IplImage colorframe = kr.getColorFrame();
		
    	cvAddWeighted(colorframe, 1.0, depthframe, level, 0.0, overlayed);
	}
}
