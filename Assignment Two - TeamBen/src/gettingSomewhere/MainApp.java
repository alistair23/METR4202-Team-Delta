package gettingSomewhere;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import functions.EdgesAndLines;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;

@SuppressWarnings("serial")
public class MainApp extends JFrame {

	JPanel img = new JPanel();
	JPanel con = new JPanel();
   public IplImage[] images = new IplImage[2];
   public String[] names = new String[5];
   public JTextArea[] console = new JTextArea[2]; 
   
   Dimension bdim = new Dimension();
    GridBagConstraints gc = new GridBagConstraints();
     
    public MainApp() {
    	super("test");
    	
    	//The Source Image
    	names[0] = "Source";
    	console[0] = new JTextArea("Opening Source Image...");
    	images[0] =	cvLoadImage("test_images/calib.png");
    	console[0].append("/n");
    	console[0].append("/n");
    	
    	
    	//Edge Detection
    	console[1] = new JTextArea("Opening Edge Image...");
    	EdgesAndLines edgeTool = new EdgesAndLines(images[0].clone());
    	names[1] = "Edge";
    	images[1] = edgeTool.getEdges();
    	console[1].append("Done.");
    	
    	
    	
		//HoughCircles
    	
		
		
		
		
		
		
		
		
		
		//Build up the GUI
    	setLayout(new GridBagLayout());
    	
    	
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Set up components preferred size
        bdim = new Dimension(this.getWidth()/images.length,20);

        setVisible(true);

        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weighty = 1;
        gc.gridwidth = 10;
        this.add(img, gc);
        //img.setEnabled(true);
        
        gc.gridy = 2;
        this.add(con,gc);
        
        
        //Add buttons for each picture in the array
    	for(int i = 0; i<images.length;i++){
    		//String name = images[i].toString();
    		final int blistener = i;
    		JButton b = new JButton(names[blistener]);
    		b.setMinimumSize(bdim);
    		b.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                        	img.removeAll();
                        	img.add(new JLabel(new ImageIcon(images[blistener].getBufferedImage())),gc);
                        	
                        	con.removeAll();
                        	con.add(console[blistener]);
                        	
                        	System.out.println("Button "+blistener+" pressed.");
                        	con.revalidate();
                        	revalidate();
                            }
                    });
            gc.anchor = GridBagConstraints.PAGE_START;
            gc.fill = GridBagConstraints.BOTH;
            gc.weightx = 1;
            gc.weighty = 0;
            gc.gridx = blistener;
            gc.gridy = 0;
            gc.gridwidth = 1;
           
            this.add(b,gc);
    	}
    	img.add(new JLabel(new ImageIcon(images[0].getBufferedImage())),gc);
    	con.add(console[0],gc);
    	this.revalidate();
    }
    public static void main(String[] args) {
    	MainApp testFrame = new MainApp();
    }
}
