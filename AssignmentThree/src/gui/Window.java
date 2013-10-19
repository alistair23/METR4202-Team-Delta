package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class creates a variation of JFrame to house IplImages within a GUI.
 * 
 */

public class Window extends JFrame {
	
    GridBagConstraints gc = new GridBagConstraints();
    GridBagLayout g = new GridBagLayout();
	
	public Window(Dimension d){
		this.setSize(d);
		this.setVisible(true);
	   	this.setLayout(g);
	}

	public JPanel ImagePanel(IplImage img, int scale){
		JPanel j = new JPanel();
        j.add(new JLabel(new ImageIcon(scale(img, scale).getBufferedImage())));
        return j;
	}
	
	public void ImagePanelUpdate(JPanel j, IplImage img, int scale){
		j.removeAll();
        j.add(new JLabel(new ImageIcon(scale(img, scale).getBufferedImage())));
        revalidate();
	}

	
	public BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}
/**	
	public IplImage scale(BufferedImage img, int scale){
		//BufferedImage bi = resize(img,img.getWidth()/scale,img.getHeight()/scale);
		//ImageConverter ic = new ImageConverter();
		//return ic.convertRGB(bi);
		return null;
	}
*/
	public IplImage scale(IplImage img, int scale){
		//BufferedImage bi = resize(img.getBufferedImage(),img.width()/scale,img.height()/scale);
		//ImageConverter ic = new ImageConverter();
		//return ic.convertRGB(bi);
		int width = (int) (((double)img.width())/((double)scale));
		int height = (int) (((double)img.height())/((double)scale));
		IplImage scaledImage = IplImage.create(width,height, img.depth(), img.nChannels());
		cvResize(img, scaledImage);
		return scaledImage;
	}
	
	public void add(Component p, int gx, int gy, int w, int h, int wx, int wy){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = gx;
        gc.gridy = gy;
        gc.weightx = wx;
        gc.weighty = wy;
        gc.gridwidth = w;
        gc.gridheight = h;
        
        this.add(p, gc);
		this.revalidate();
	}

	public void exit(){
		System.exit(0);
	}
}