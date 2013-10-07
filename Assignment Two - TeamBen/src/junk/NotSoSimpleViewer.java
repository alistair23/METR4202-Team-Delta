package junk;
// this file was stolen from the OpenNI examples and is just used to simply display video streams

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cameraCalibration.CameraCalibration4;
import cameraCalibration.CameraCalibrator;
import capture.KinectReader;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class NotSoSimpleViewer implements KeyListener  {
    
	static KinectReader kr = new KinectReader();

	JFrame window = new JFrame();
    
    public NotSoSimpleViewer() {
    	window.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		     
		    	System.out.println("Closing");
		    	kr.Stop();
		    	
		    	System.exit(0);
		    }
		});
    }
    
    public static void main(String[] args) {
  		
		kr.Start();
		
		IplImage image = kr.getColorFrame();
		JFrame window = new JFrame();
		JPanel con = new JPanel();
		
		window.setSize(image.width(), image.height());
		
		con.add(new JLabel(new ImageIcon(image.getBufferedImage())));
		window.revalidate();
		
		CameraCalibrator cc = new CameraCalibrator();
		
		
		
		 window.getContentPane().add(con);
		
		window.setVisible(true);
		
		//IplImage undistortedImage = cc.remap(image);
		
		while(1 == 1){

			cc.setup();
		
			while(cc.SampleAt < cc.Samples){
				
				image = kr.getColorFrame();
				cc.addToCalibration(image);
				con.removeAll();
				con.add(new JLabel(new ImageIcon(cc.FindChessboard(image).getBufferedImage())));
				window.revalidate();
				
			}
			
			System.out.println("**********"+cc.calibrate()+"***********");
			cc.SampleAt = 0;
		}
    }

			
		

	   public void keyPressed(KeyEvent event) {
	    	if(event.getKeyChar() == 'a'){
	    		System.out.println("Closing");
		    	kr.Stop();
		    	
		    	System.exit(0);
	    	}
		}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
    
    
}
