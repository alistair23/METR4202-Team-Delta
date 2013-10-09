package capture;

import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OverlayImage implements Runnable {

	KinectReader kr;
	IplImage overlayed;
	
    public OverlayImage(KinectReader kr) {
        this.kr = kr;
        
    }
	
	@Override
	public void run() {
		IplImage overlayed = kr.getDepthFrame();
		IplImage depthframe = kr.getDepthFrame();
		IplImage colorframe = kr.getColorFrame();
		
	//	AxisLocator al = new AxisLocator(colorframe);
	//	al.findAxis(colorframe);
		
		if (depthframe.width() == colorframe.width()) {
    		cvAddWeighted(colorframe, 1.0, depthframe, 0.5, 0.0, overlayed);
		}
		
	}

}
