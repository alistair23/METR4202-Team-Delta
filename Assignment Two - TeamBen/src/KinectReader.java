import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;


public class KinectReader {

	Device device;
	VideoStream Cstream;
	VideoStream Dstream;
	VideoFrameRef Cframe;
	VideoFrameRef Dframe;
	private SimpleViewer viewer;
	private SimpleViewer viewer2;
	private JFrame windowFrame;
	
	public void Start(){
		System.out.println("Starting Kinect");
		OpenNI.initialize();
		
		//Check for connected devices
		 List<DeviceInfo> devicesInfo = OpenNI.enumerateDevices();
	        if (devicesInfo.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "No device is connected", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }
	        
	     //TODO check that the device has depth, color, and IR sensors
	        //device.hasSensor(SensorType.COLOR);
	        //device.hasSensor(SensorType.IR);
	        //device.hasSensor(SensorType.DEPTH);
	        
	        device = Device.open(devicesInfo.get(0).getUri());
		
	        	        	        
	        Cstream = VideoStream.create(device, SensorType.COLOR);
	        Dstream = VideoStream.create(device, SensorType.DEPTH);
	        
	        Cstream.setVideoMode(Cstream.getSensorInfo().getSupportedVideoModes().get(1));
	        Dstream.setVideoMode(Dstream.getSensorInfo().getSupportedVideoModes().get(0));
	        
	        Cstream.start();
	        Dstream.start();
	        
	}
	
	public void Stop(){
		Cstream.stop();
		//Cstream.destroy();
		Dstream.stop();
		//Dstream.destroy();
		device.close();
		windowFrame.dispose();
		//OpenNI.shutdown();
		
	}
	
	//TODO this should get the current frame of RGBD data and output it to a file or object that is useful
	public void getFrame(){
		System.out.println("Getting Frames: Depth");
		Dframe = Dstream.readFrame();
		System.out.println("Getting Frames: Color");
		Cframe = Cstream.readFrame();
	}
	
	public void showFrame(){
		System.out.println("Showing Frames");
		
		viewer = new SimpleViewer();
		viewer.setSize(Cframe.getWidth(),Cframe.getHeight());
		
		viewer2 = new SimpleViewer();
		viewer2.setSize(Dframe.getWidth(),Dframe.getHeight());
		
		windowFrame = new JFrame("Lab2 Frame Viewer");
		windowFrame.add(viewer2, BorderLayout.LINE_START);
		windowFrame.add(viewer, BorderLayout.LINE_END);
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
		
		//System.out.println(Cframe.toString());
	}
	
}
