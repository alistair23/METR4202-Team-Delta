package capture;

import static org.openni.PixelFormat.DEPTH_1_MM;
import gui.videoPanel;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoStream;



import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class CameraReader {

//the number (0) refers to the device number. if you only have one camera, it will be 0, if you have more, increment it.
	final OpenCVFrameGrabber device = new OpenCVFrameGrabber(0);
	

	IplImage cFrame;
	
	public static void main(String[] args) {
		
		CameraReader cr = new CameraReader();
		cr.Start();
		
		JFrame w = new JFrame();
		w.setSize(1300,900);
		w.setVisible(true);
		
		videoPanel v = new videoPanel(cr);
		w.add(v);
		v.run();
		
		
		
		//if(cr.Start()){
			cr.getColorFrame();
		//}
	
	}
	
	public boolean Start(){
		System.out.println("Starting Camera");
		
		try {
			device.start();
		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
			return false;
		}
		
		
        return true;
	}
	
	
 	
	public void Stop(){
		try {
			device.flush();
		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IplImage getColorFrame(){
		
		try {
            cFrame = device.grab();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return cFrame;
		
	}
	

	

	

}
