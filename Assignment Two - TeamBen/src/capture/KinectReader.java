package capture;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import static org.openni.PixelFormat.*;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoStream;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * 
 * @author Benjamin Rose & Ben Merange
 *
 * This Class Streams a connected kinect device (installed using kinect SDK 1.7 Drivers and OPENNI2.2)
 *
 * when viewing the GUI, press 'a' to recapture both still frames, 'd' to save the current frame, 
 * and 's' to both recapture and save the frames. 
 * 
 * The exposed variables 'CBuffer' 'DBuffer' 'CFrame' and 'DFrame' all store the current frames.
 *
 * Typical use for Image capture:
 
 		KinectReader kr = new KinectReader();
 		kr.Start();
 		
 		//the following gets IplImages
 		IplImage colorImage = kr.getColorFrame();
 		IplImage depthImage = kr.getDepthFrame();
 		
 		//the Following gets Buffered Images
 		//ImageConverter ic = new ImageConverter();
 		//BufferedImage colorImage = ic.convertRGB(kr.getColorFrame());
 		//BufferedImage depthImage = ic.convertD(kr.getDepthFrame());
 
 		//The following shows a GUI 
 		//kr.showFrames();
 		//kr.showStreams();
 
 */

public class KinectReader {

	private Device device;
	private VideoStream Cstream;
	private VideoStream Dstream;
	private JFrame windowFrame;
	private JPanel CPanel = new JPanel();
	private JPanel DPanel = new JPanel();
	BufferedImage CBuffer;
	BufferedImage DBuffer;
	VideoFrameRef Cframe;
	VideoFrameRef Dframe;

	int imageindex = 0;
	
	public int frames;
	
	ImageConverter ic = new ImageConverter();
	
	public KinectReader(){
	}
	
	public static void main(String[] args) {
		KinectReader kr = new KinectReader();
		if(kr.Start()){
			kr.getColorFrame();
		}
		//kr.getDepthFrame();
	}
	
	public boolean deviceConnected() {
		if (device == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean Start(){
		System.out.println("Starting Kinect");
		OpenNI.initialize();
		
		//Check for connected devices
		List<DeviceInfo> devicesInfo = OpenNI.enumerateDevices();
	    if (devicesInfo.isEmpty()) {
           // JOptionPane.showMessageDialog(null, "No device is connected", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
	    }else{
	        
        device = Device.open(devicesInfo.get(0).getUri());
        Cstream = VideoStream.create(device, SensorType.COLOR);
        Dstream = VideoStream.create(device, SensorType.DEPTH);
        
        Cstream.setVideoMode(Cstream.getSensorInfo().getSupportedVideoModes().get(1));
        Dstream.setVideoMode(Dstream.getSensorInfo().getSupportedVideoModes().get(0));
        Dstream.getVideoMode().setPixelFormat(DEPTH_1_MM);
        
        Cstream.start();
        Dstream.start();
        return true;
	    }
	}
	
 	
	public void Stop(){
		device.close();
		Cstream.destroy();
		Dstream.destroy();
		windowFrame.dispose();
		
	}
	
	public IplImage getColorFrame(){
		
		Cframe = Cstream.readFrame();
		
		IplImage ii = ic.convertRGB(ic.convertRGB(Cframe));
		
		frames++;
		return ii;
		
	}
	
	public IplImage getDepthFrame(){
		
        Dframe = Dstream.readFrame();
		
        DBuffer = ic.convertD(Dframe, Dstream);
        
        DPanel.removeAll();
		DPanel.add(new JLabel(new ImageIcon(DBuffer)));        
  		
		IplImage ii = IplImage.createFrom(DBuffer);
		return ii;
	}
	
	public void getFrames(){
		getColorFrame();
		getDepthFrame();
	}
	
	public IplImage getHighResImage(){
		Cstream.stop();
		Cstream.setVideoMode(Cstream.getSensorInfo().getSupportedVideoModes().get(0));
		Cstream.start();
		IplImage hires = getColorFrame();
		Cstream.stop();
		Cstream.setVideoMode(Cstream.getSensorInfo().getSupportedVideoModes().get(1));
		Cstream.start();
		return hires;
	}
}