package capture;

import gui.videoPanel;
import java.awt.Dimension;
import javax.swing.JFrame;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class CameraReader {

//the number (0) refers to the device number. if you only have one camera, it will be 0, if you have more, increment it.
	
	CvCapture capture;

	IplImage cFrame;

	public CameraReader(){
		capture = opencv_highgui.cvCreateCameraCapture(1);
	}

	public CameraReader(int dev){
		capture = opencv_highgui.cvCreateCameraCapture(dev);
	}
	
	public static void main(String[] args) {
		CameraReader cr = new CameraReader(1);
		cr.Start();
		JFrame w = new JFrame();
		w.setSize(new Dimension(cr.getColorFrame().width()+20,cr.getColorFrame().height()+50));
		w.setVisible(true);
		videoPanel v = new videoPanel(cr);
		w.add(v);
		v.run();
	}
	
	public boolean Start(){
		
		System.out.println("Starting Camera");
		opencv_highgui.cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
	    opencv_highgui.cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
		return true;
	}
	
	public void Stop(){
		// TODO Auto-generated catch block
	}
	
	public IplImage getColorFrame(){
		IplImage grabbedImage = opencv_highgui.cvQueryFrame(capture);
		return grabbedImage.clone();
	}
	public IplImage getDepthFrame(){
		return null;
	}
	public IplImage getOverlayFrame(){
		return null;
	}
	
	public boolean listCameras(){
		return true;
	}
}
