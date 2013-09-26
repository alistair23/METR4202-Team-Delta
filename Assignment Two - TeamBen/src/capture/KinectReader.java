package capture;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoStream;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * 
 * @author Benjamin Rose
 *
 *This Class Streams a connected kinect device (installed using kinect SDK 1.7 Drivers and OPENNI2.2)
 *
 * when viewing the GUI, press 'a' to recapture both still frames, 'd' to save the current frame, 
 * and 's' to both recapture and save the frames. 
 * 
 * The exposed variables 'CBuffer' 'DBuffer' 'CFrame' and 'DFrame' all store the current frames.
 *
 *Typical use for Image capture:
 
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

public class KinectReader implements KeyListener {

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
	private SimpleViewer viewer;
	private SimpleViewer viewer2;
	int imageindex = 0;
	
	ImageConverter ic = new ImageConverter();
	
	public void Start(){
		System.out.println("Starting Kinect");
		OpenNI.initialize();
		
		//Check for connected devices
		List<DeviceInfo> devicesInfo = OpenNI.enumerateDevices();
	    if (devicesInfo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No device is connected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
	    }
	        
        device = Device.open(devicesInfo.get(0).getUri());
	
        Cstream = VideoStream.create(device, SensorType.COLOR);
        Dstream = VideoStream.create(device, SensorType.DEPTH);
        
        Cstream.setVideoMode(Cstream.getSensorInfo().getSupportedVideoModes().get(1));
        Dstream.setVideoMode(Dstream.getSensorInfo().getSupportedVideoModes().get(0));
        
        Cstream.start();
        Dstream.start();
        
        windowFrame = new JFrame("Lab2 Frame Viewer");
        windowFrame.setLayout(new GridLayout(0,2));
        windowFrame.addKeyListener(this);
        
        windowFrame.getContentPane().add(CPanel);
		windowFrame.getContentPane().add(DPanel);
  
		CBuffer = ic.convertRGB(Cstream.readFrame());
		DBuffer = ic.convertD(Dstream.readFrame());
		
		CPanel.add(new JLabel(new ImageIcon(CBuffer)));
		DPanel.add(new JLabel(new ImageIcon(DBuffer)));
	
	}
	
 	
	public void Stop(){
		device.close();
		Cstream.destroy();
		Dstream.destroy();
		windowFrame.dispose();
		
	}
	
	public IplImage getColorFrame(){

		//System.out.println("Getting Frames: Color");
		Cframe = Cstream.readFrame();
		
		CBuffer = ic.convertRGB(Cframe);
		
		CPanel.removeAll();
		CPanel.add(new JLabel(new ImageIcon(CBuffer)));
		
		IplImage ii = IplImage.createFrom(CBuffer);

		return ii;
	}
	
	public IplImage getDepthFrame(){
		//System.out.println("Getting Frames: Depth");
        Dframe = Dstream.readFrame();
		
        DBuffer = ic.convertD(Dframe);
        
        DPanel.removeAll();
		DPanel.add(new JLabel(new ImageIcon(DBuffer)));        
        
  		
		IplImage ii = IplImage.createFrom(DBuffer);
		 
		return ii;
	}
	
	public void showStreams(){
		System.out.println("Showing Streams");
		
		viewer = new SimpleViewer();
		viewer.setSize(Cframe.getWidth(),Cframe.getHeight());
		
		viewer2 = new SimpleViewer();
		viewer2.setSize(Dframe.getWidth(),Dframe.getHeight());
		
		windowFrame.add(viewer2,0);
		
		windowFrame.add(viewer,0);
		
		windowFrame.setVisible(true);
		windowFrame.setSize(1920, 1200);
		
		viewer.setStream(Cstream);
		viewer2.setStream(Dstream);
		
		windowFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		     
		    	System.out.println("Closing");
		    	Stop();
		    	
		    	System.exit(0);
		    }
		});
	}
	
	public void getFrames(){
		getColorFrame();
		getDepthFrame();
	}
	
	   public void keyPressed(KeyEvent event) {
	    	if(event.getKeyChar() == 'a'){
	    		getFrames();
	    		windowFrame.revalidate();
	    	}else if(event.getKeyChar() == 's'){
	    		getFrames();
	    		windowFrame.revalidate();
	    		ic.savePNG("img-"+imageindex+"-C", Cframe);
	    		ic.savePNG("img-"+imageindex+"-D", Dframe);
	    		imageindex++;
	    	}else if(event.getKeyChar() == 's'){
				ic.savePNG("img-"+imageindex+"-C", Cframe);
				ic.savePNG("img-"+imageindex+"-D", Dframe);
				imageindex++;
	    	}
		}

		

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
	public void setHighRes(){
		Cstream.setVideoMode(Cstream.getSensorInfo().getSupportedVideoModes().get(0));
	}
	
}