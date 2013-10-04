package gui;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.ArrayList;
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

import colorCalibration.ColorChart;
import colorCalibration.BlackBalance;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import functions.CoinFinder;
import functions.ImageRectifier;

public class gridWindow extends JFrame {

    GridBagConstraints gc = new GridBagConstraints();
    GridBagLayout g = new GridBagLayout();
    Dimension d = new Dimension(1200, 1000);
    static boolean imgtype = true;
    final static JTextArea con = new JTextArea();
    
 
    public static ArrayList<Integer> depthPickerData = new ArrayList<Integer>();
    private static IplImage colorImage;
    private static IplImage depthImage;
    private static CvScalar BLACK = null;
    private static IplImage rectifiedImage = null;
    
    public gridWindow(){
    	
    	this.setLayout(g);
        this.setVisible(true);
        this.setSize(d);
    }
    
    public static void main(String[] args) {
    	final gridWindow gw = new gridWindow();
        final KinectReader kr = new KinectReader();
    	
    	kr.Start();
		
		IplImage Mainimage = kr.getColorFrame();
		IplImage image = cvLoadImage("test_images/ti.png");
		
    	//IplImage img = cvLoadImage("test_images/colorchart.png");
    	
    	 
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
    	JButton b16 = new JButton("Find Coins");
    	JButton b17 = new JButton("Color Calibrator");
    	JButton b18 = new JButton("Pick Depth Data");
    	JButton b19 = new JButton("Set Black");
    	
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
    	
		final DepthPicker depthPicker = new DepthPicker();
        depthPicker.addWindowListener(
        		new WindowAdapter() { 
        			public void windowClosing(WindowEvent e) { 
        				gw.setEnabled(true);
        				depthPicker.setVisible(false);
        				depthPickerData = depthPicker.getCoords();
        				if (depthPickerData != null) {
        					for (Integer coord : depthPickerData) {
        						con.append(coord.toString()+", ");
        				    }
        				    con.append("\n");
        				    
        				    rectifyImage(gw, P02, P03, s);
        				    
        			    } else {
        			    	con.append("Bad selection.\n");
        			    }
        }});
		
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
      		 // COIN FINDER
            public void actionPerformed(ActionEvent e)
            {	
            	if (rectifiedImage == null) {
            		con.append("Rectify image from depth first!");
            	} else {
	            	CoinFinder coinFinder = new CoinFinder(rectifiedImage);
	            	coinFinder.find();
	            	// ASSUMES THAT A PLATE IS BEING USED!
	            	// CAN EASILY MODIFY TO BYPASS THIS
            	}
            }
        });      
    	
    	b17.addActionListener(new ActionListener() {
      		// COLOR CALIBRATION
            public void actionPerformed(ActionEvent e)
            {	
        		//IplImage grabImage = cvLoadImage("test_images/chart.png");
        		
        		if (BLACK != null) {
        			IplImage grabImage = kr.getColorFrame();
            		gw.addPanel(P05, grabImage, s);
	        		ColorChart chart = new ColorChart(grabImage, BLACK);
	        		if (! chart.findCalibColors()) {
	        			con.append("Cannot find colors!\n");
	        			//System.out.println("Cannot find colors!");
	        		} else {
	        			gw.addPanel(P06, chart.getGoldImg(), s);
		        		gw.addPanel(P07, chart.getSilverImg(), s);
		        		con.append(chart.getColorData());
	        		}
	        		gw.validate();
        		}
        		else {
        			con.append("Set black first!");
        		}
            }
        });      

    	b18.addActionListener(new ActionListener() {
    		// DEPTH PICKER AND RECTIFICATION
            public void actionPerformed(ActionEvent e)
            {	
            	//IplImage depthImage = cvLoadImage("test_images/trialcount_depth.png");
            	// 640 x 480
            	colorImage = kr.getColorFrame();
            	depthImage = kr.getDepthFrame();
            	
            	//colorImage = cvCreateImage(cvGetSize(colorImageRGB),8,3);
            	//depthImage = cvCreateImage(cvGetSize(depthImageInput),8,1);
            	
        		//cvCvtColor(depthImageInput, depthImage, CV_BGR2GRAY);
            	
        		//cvShowImage("afdsadfs", depthImage);
        		//cvWaitKey(0);
        		
            	depthPicker.setImage(depthImage);
            	depthPicker.setVisible(true);
            	
            	con.append("Pick a region...\n");
            	gw.setEnabled(false);
            	
            	// RECTIFICATION INITIATED ON WINDOW CLOSE
            }
        });      

    	b19.addActionListener(new ActionListener() {
      		 
            public void actionPerformed(ActionEvent e)
            {
    			//gw.exit();
            	
            	IplImage blackimg = kr.getColorFrame();
            	//IplImage blackimg = cvLoadImage("test_images/black.png");
            	
            	BlackBalance blackBal = new BlackBalance(blackimg);
            	BLACK = blackBal.getHsvValues();
            	con.append("Black Set.\n");
            }
        });
    	
    	gw.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
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

    private static void rectifyImage(gridWindow gw, JPanel P02, JPanel P03, int s) {
    	
    	if (depthPickerData.isEmpty()) {
    		con.append("Pick a region first!\n");
    	} else {
    		//System.out.println(depthPickerData);
        	//IplImage colorImage = cvLoadImage("test_images/trialcount_img.png");
    		//IplImage depthImage = cvLoadImage("test_images/trialcount_depth.png");
        	
        	ImageRectifier rectifyImage = new ImageRectifier(colorImage, depthImage, depthPickerData);
    		IplImage trialTable = rectifyImage.drawTableLines();
    		gw.addPanel(P02, trialTable, s);
    		con.append(rectifyImage.getDepthData().toString());
    		
    		rectifiedImage = rectifyImage.transformImage();
    		gw.addPanel(P03, rectifiedImage, s);
    		
    		//cvWaitKey(0);
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