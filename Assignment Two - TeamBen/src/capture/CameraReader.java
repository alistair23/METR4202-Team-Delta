package capture;

import gui.videoPanel;

import java.util.List;

import javax.swing.JFrame;

import org.openni.DeviceInfo;
import org.openni.OpenNI;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class CameraReader {

//the number (0) refers to the device number. if you only have one camera, it will be 0, if you have more, increment it.
	
	OpenCVFrameGrabber device = new OpenCVFrameGrabber(2);
	

	IplImage cFrame;
	

	public CameraReader(){
		
	}

	public CameraReader(int dev){
		device = new OpenCVFrameGrabber(dev);
	}
	
	
	public static void main(String[] args) {
		
		CameraReader cr = new CameraReader();
		cr.listCameras();
		cr.Start();
		
		JFrame w = new JFrame();
		w.setSize(1300,900);
		w.setVisible(true);
		
		videoPanel v = new videoPanel(cr);
		w.add(v);
		v.run();
	
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
	public IplImage getDepthFrame(){
		return null;
	}
	public IplImage getOverlayFrame(){
		return null;
	}
	
	public boolean listCameras(){
		List<DeviceInfo> devicesInfo = OpenNI.enumerateDevices();
	    if (devicesInfo.isEmpty()) {
           // JOptionPane.showMessageDialog(null, "No device is connected", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
            
	    }else{
	    	System.out.println(devicesInfo.toString());
	    	return true;
	    }
	}
}
