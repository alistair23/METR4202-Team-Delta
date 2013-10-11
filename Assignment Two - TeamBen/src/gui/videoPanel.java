package gui;

import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import capture.CameraReader;
import capture.ImageConverter;
import capture.KinectReader;

public class videoPanel extends JPanel implements Runnable{

	static KinectReader kr;
	static CameraReader cr;
	IplImage ic;
	IplImage id;
	 IplImage iv;
	String output = "ic";
	int scale = 1;
		
	public videoPanel(){
		kr = new KinectReader();
		kr.Start();
		iv = kr.getColorFrame();
		this.add(new JLabel(new ImageIcon(iv.getBufferedImage())));
		

		
		//this.run();
	}
	
	public videoPanel(KinectReader kr){
		this.kr = kr;
		//kr.Start();
		iv = kr.getColorFrame();
		//this.add(new JLabel(new ImageIcon(io.getBufferedImage())));

		
		//this.run();
	}
	
	public videoPanel(CameraReader cr){
		this.cr = cr;
		cr.Start();
		//kr.Start();
		iv = cr.getColorFrame();
		//this.add(new JLabel(new ImageIcon(io.getBufferedImage())));

		
		//this.run();
	}
	
	public static void main(String[] args) {
		
		JFrame w = new JFrame();
		w.setSize(700, 700);
		w.setVisible(true);
		
		videoPanel v = new videoPanel(cr);
		w.add(v);
		v.run();
	}

@Override
	public void run() {
		while(true){
			ic = cr.getColorFrame();
			//id = cr.getDepthFrame();
			iv = ic.clone();
			//cvAddWeighted(ic, 1.0, id, 0.5, 0.0, iv);
			this.removeAll();
			if(output == "iv"){
				this.add(new JLabel(new ImageIcon(scale(iv, scale).getBufferedImage())));
			}else if(output == "ic"){
				this.add(new JLabel(new ImageIcon(scale(ic, scale).getBufferedImage())));
			}else if(output == "id"){
				this.add(new JLabel(new ImageIcon(scale(id, scale).getBufferedImage())));
			}
			this.revalidate();
			System.out.println("Doing");
		}
		//System.out.println("Done");
		
	}
	
	public IplImage scale(IplImage img, int scale){
		BufferedImage bi = resize(img.getBufferedImage(),img.width()/scale,img.height()/scale);
		ImageConverter ic = new ImageConverter();
		return ic.convertRGB(bi);
	}
	
	public BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}

}
