package gui;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cameraCalibration.CameraCalibrator;
import capture.ImageConverter;
import capture.KinectReader;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class gridWindow extends JFrame {

    GridBagConstraints gc = new GridBagConstraints();
    GridBagLayout g = new GridBagLayout();
    Dimension d = new Dimension(1200, 1000);
    static boolean imgtype = true;
    final static JTextArea con = new JTextArea();
 
    
    public gridWindow(){
    	
    	this.setLayout(g);
        this.setVisible(true);
        this.setSize(d);
    }
    
    public static void main(String[] args) {
    	
        final KinectReader kr = new KinectReader();
  	  
    	
    	kr.Start();
		
		IplImage Mainimage = kr.getColorFrame();
		IplImage image = cvLoadImage("test_images/ti.png");
		
    	//IplImage img = cvLoadImage("test_images/colorchart.png");
    	
    	 final gridWindow gw = new gridWindow();
    	//JPanel p = new JPanel();
    	//p.add(new JLabel("PANELLLLL"));
    	
    	final int s = 3; //sample image scaling factor
    	
    	 final JPanel P01 = new JPanel();
    	gw.addPanel(P01, image, s);
    	final JPanel P02 = new JPanel();
    	gw.addPanel(P02, image, s);
    	final JPanel P03 = new JPanel();
    	gw.addPanel(P03, image, s);
    	final JPanel P04 = new JPanel();
    	gw.addPanel(P04, image, s);
    	final JPanel P05 = new JPanel();
    	gw.addPanel(P05, image, s);
    	final JPanel P06 = new JPanel();
    	gw.addPanel(P06, image, s);
    	final JPanel P07 = new JPanel();
    	gw.addPanel(P07, image, s);
    	final JPanel P08 = new JPanel();
    	gw.addPanel(P08, image, s);
    	final JPanel P09 = new JPanel();
    	gw.addPanel(P09, image, s);
    	final JPanel P10 = new JPanel();
    	gw.addPanel(P10, image, s);
    	final JPanel P11 = new JPanel();
    	gw.addPanel(P11, image, s);
    	final JPanel P12 = new JPanel();
    	gw.addPanel(P12, image, s);
    	final JPanel P13 = new JPanel();
    	gw.addPanel(P13, image, s);

    	final JPanel PM = new JPanel();
    	gw.addPanel(PM, Mainimage, 1);
    	
    	
    	//con.append( "Hello World.\n" );
    	con.setLineWrap(true);
    	con.setWrapStyleWord(true);

    	final JScrollPane sp = new JScrollPane();
    	sp.getViewport ().setView ( con );

    	gw.addPanel(sp, 1, 4, 3, 1);
    	
   	
    	JButton b15 = new JButton("Calibrate");
    	b15.setMaximumSize(new Dimension(200, 30));
    	JButton b16 = new JButton("Capture");
    	JButton b17 = new JButton("Detect Edges");
    	JButton b18 = new JButton("Detect Circles");
    	JButton b19 = new JButton("Exit");
    	
    	gw.addPanel(P01, 0, 4, 1, 1);
    	gw.addPanel(P02, 0, 3, 1, 1);
    	gw.addPanel(P03, 0, 2, 1, 1);
    	gw.addPanel(P04, 0, 1, 1, 1);
    	gw.addPanel(P05, 0, 0, 1, 1);
    	
    	gw.addPanel(P06, 1, 0, 1, 1);
    	gw.addPanel(P07, 2, 0, 1, 1);
    	gw.addPanel(P08, 3, 0, 1, 1);
    	
    	gw.addPanel(P09, 4, 0, 1, 1);
    	gw.addPanel(P10, 4, 1, 1, 1);
    	gw.addPanel(P11, 4, 2, 1, 1);
    	gw.addPanel(P12, 4, 3, 1, 1);
    	gw.addPanel(P13, 4, 4, 1, 1);

    	gw.addPanel(PM, 1, 1, 3, 4);
    	    	
    	gw.addPanel(b15, 0, 5, 1, 1);
    	gw.addPanel(b16, 1, 5, 1, 1);
    	gw.addPanel(b17, 2, 5, 1, 1);
    	gw.addPanel(b18, 3, 5, 1, 1);
    	gw.addPanel(b19, 4, 5, 1, 1);
    	
    	final CameraCalibrator cc = new CameraCalibrator();
		cc.setup();
    	
    	b15.addActionListener(new ActionListener() {
   		 
            public void actionPerformed(ActionEvent e)
            {
    			
    			
    			//for(int i = 0; cc.SampleAt < cc.Samples; i++){
    				IplImage image = kr.getColorFrame();
    				cc.addToCalibration(image);
    				
    				
    				//gw.addPanel(PM, cc.FindChessboard(image), 1);

    				if (cc.SampleAt == 1){
    					gw.addPanel(P01, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 2){
    					gw.addPanel(P02, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 3){
    					gw.addPanel(P03, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 4){
    					gw.addPanel(P04, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 5){
    					gw.addPanel(P05, cc.FindChessboard(image), s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 6){
    					gw.addPanel(P06, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 7){
    					gw.addPanel(P07, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 8){
    					gw.addPanel(P08, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 9){
    					gw.addPanel(P09, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 10){
    					gw.addPanel(P10, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 11){
    					gw.addPanel(P11, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 12){
    					gw.addPanel(P12, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} else if (cc.SampleAt == 13){
    					gw.addPanel(P13, image, s);
    					con.append("Collecting Sample Image "+cc.SampleAt+"/"+cc.Samples+"\n");
    				} 
    				gw.validate();
    				    				
    			//}
    			if(cc.SampleAt == cc.Samples){
    				con.append("All Samples Collected. Peforming Calibration...\n");
    			con.append("Calibration Error = "+cc.calibrate()+"\n");

    			cc.SampleAt = 0;
    			}
            }
        });      
    	
    	b16.addActionListener(new ActionListener() {
      		 
            public void actionPerformed(ActionEvent e)
            {
            	ImageConverter ic = new ImageConverter();
            	
           	
            	String str = (String)JOptionPane.showInputDialog( null, "Enter the File Name:", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, null, "Image");
            	
            	ic.savePNG(str, kr.getColorFrame());
            	
            }
        });      

    	b17.addActionListener(new ActionListener() {
      		 
            public void actionPerformed(ActionEvent e)
            {
            }
        });      

    	b18.addActionListener(new ActionListener() {
      		 
            public void actionPerformed(ActionEvent e)
            {
            }
        });      

    	b19.addActionListener(new ActionListener() {
      		 
            public void actionPerformed(ActionEvent e)
            {
    			gw.exit();
            }
        });      

    	while(1==1){
    		if(imgtype == true){
    		Mainimage = cc.FindChessboard(kr.getColorFrame());
    		}else{
    			Mainimage = kr.getDepthFrame();
    		}
    			
    		gw.addPanel(PM, Mainimage, 1);
    	}
    	
    	
  
    }
    
    

	public void addPanel(Component p, int x, int y, int w, int h){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = w;
        gc.gridheight = h;
        
       // this.remove(x,y);

        
        
        this.add(p, gc);
        
		//this.add(p,gc);
		this.revalidate();
		
	}
	
	public void addPanel(IplImage p, int x, int y, int w, int h){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = w;
        gc.gridheight = h;

        //this.remove(x,y);

        
        this.add(new JLabel(new ImageIcon(p.getBufferedImage())), gc);
        
        //this.remove(x,y);
		//this.add(p,gc);
		this.revalidate();
		
	}
	
	public void addPanel(IplImage p, int x, int y, int w, int h, int scale){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = w;
        gc.gridheight = h;

        
       // this.remove(x,y);

        this.add(new JLabel(new ImageIcon(resize(p.getBufferedImage(),p.width()/scale,p.height()/scale))), gc);
        
		//this.add(p,gc);
		this.revalidate();
		
	}
	
	public void addPanel(JPanel jp, IplImage p, int scale){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1;
        gc.weighty = 1;
        
		
		
		jp.removeAll();
        jp.add(new JLabel(new ImageIcon(resize(p.getBufferedImage(),p.width()/scale,p.height()/scale))),gc);
        
		this.revalidate();
		
	}
	
	public void remove(int x, int y){
		if(this.getContentPane().contains(x, y)){
			this.getContentPane().remove(this.getContentPane().getComponentAt(x, y));
		}
	}



	public static BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	
	}
	
	public void exit(){
		System.exit(0);
	}
	

}