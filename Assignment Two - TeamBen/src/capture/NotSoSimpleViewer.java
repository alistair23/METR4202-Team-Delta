package capture;
// this file was stolen from the OpenNI examples and is just used to simply display video streams

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openni.*;

import cameraCalibration.CameraCalibrator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class NotSoSimpleViewer implements KeyListener  {
    


    
    public NotSoSimpleViewer() {
    }
    
    public static void main(String[] args) {
  		KinectReader kr = new KinectReader();
		kr.Start();
		
		IplImage image = kr.getColorFrame();
		JFrame window = new JFrame();
		JPanel con = new JPanel();
		
		window.setSize(image.width(), image.height());
		
		CameraCalibrator cc = new CameraCalibrator();
		
		
		
		 window.getContentPane().add(con);
		
		window.setVisible(true);
		
		while(1 == 1){
		image = kr.getColorFrame();
		con.removeAll();
		con.add(new JLabel(new ImageIcon(cc.FindChessboard(image).getBufferedImage())));
		window.revalidate();
		}
		
		
		
		
    }

	   public void keyPressed(KeyEvent event) {
	    	if(event.getKeyChar() == 'a'){
	    		
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
