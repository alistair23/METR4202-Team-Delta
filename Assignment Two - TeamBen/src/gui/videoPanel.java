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

	static CameraReader cr;
	IplImage ic;
	IplImage id;
	IplImage iv;
	String output = "Color";// Overlay or Depth
	int scale = 1;
	public Dimension size = new Dimension(700,700);
		
	public videoPanel(){
		cr = new CameraReader();
		cr.Start();
		this.size = new Dimension(cr.getColorFrame().width()+20,cr.getColorFrame().height()+50);

		iv = cr.getColorFrame();
		this.add(new JLabel(new ImageIcon(iv.getBufferedImage())));
		

		
		//this.run();
	}
	
	public videoPanel(KinectReader kr){
		this.cr = kr;
		//kr.Start();
		this.size = new Dimension(kr.getColorFrame().width()+20,kr.getColorFrame().height()+50);
		iv = cr.getColorFrame();
		//this.add(new JLabel(new ImageIcon(io.getBufferedImage())));

		
		//this.run();
	}
	
	public videoPanel(KinectReader kr, String str){
		this.cr = kr;
		//kr.Start();
		this.size = new Dimension(kr.getColorFrame().width()+20,kr.getColorFrame().height()+50);

		output = str;
		
		iv = cr.getColorFrame();
		//this.add(new JLabel(new ImageIcon(io.getBufferedImage())));

		
		//this.run();
	}
	
	public videoPanel(CameraReader cr){
		this.cr = cr;
		this.size = new Dimension(cr.getColorFrame().width(),cr.getColorFrame().height());

		cr.Start();
		//kr.Start();
		iv = cr.getColorFrame();
		//this.add(new JLabel(new ImageIcon(io.getBufferedImage())));

		
		//this.run();
	}
	
	public static void main(String[] args) {
		videoPanel v = new videoPanel();
		JFrame w = new JFrame();
		w.setSize(v.size);
		w.setVisible(true);

		w.add(v);
		v.run();
	}

@Override
	public void run() {
		while(true){
			ic = cr.getColorFrame();
			id = cr.getDepthFrame();
			//iv = cr.getOverlayFrame();
			//cvAddWeighted(ic, 1.0, id, 0.5, 0.0, iv);
			this.removeAll();
			if(output == "Overlay"){
				this.add(new JLabel(new ImageIcon(scale(iv, scale).getBufferedImage())));
			}else if(output == "Color"){
				this.add(new JLabel(new ImageIcon(scale(ic, scale).getBufferedImage())));
			}else if(output == "Depth"){
				this.add(new JLabel(new ImageIcon(scale(id, scale).getBufferedImage())));
			}
			this.revalidate();
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
