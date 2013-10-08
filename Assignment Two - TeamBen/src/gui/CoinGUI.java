package gui;

import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGBA2RGB;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvRodrigues2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import localisation.AxisLocator;
import colorCalibration.BlackBalance;
import colorCalibration.ColorChart;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import cameraCalibration.CameraCalibrator;
import capture.ImageConverter;
import capture.KinectReader;
import functions.CoinFinder;
import functions.ImageRectifier;


public class CoinGUI extends JFrame{

	static ConsolePanel con = new ConsolePanel();
    
	static JPanel samples = new JPanel();
	
	static IplImage mainI;
	static IplImage currentI;
	static IplImage currentDI;

	//Camera Calibration Variables
	static CvMat objectPoints;
	static CvMat imagePoints;
 	static CvMat cameraMatrix;	
 	static CvMat distCoeffs;
 	static CvMat rotVectors;
 	static CvMat transVectors;
 	static CvSize Resolution;
 	static CvMat mapx;
 	static CvMat mapy;
 	static Double error;
	
	//Color Calibration
	private static CvScalar BLACK = null;
	
	//Depth Picker and coin finder
    public static ArrayList<Integer> depthPickerData = new ArrayList<Integer>();
    private static IplImage colorImage;
    private static IplImage depthImage;
    private static IplImage rectifiedDepth = null;
    private static IplImage rectifiedImage = null;
    private static IplImage trialTable = null;
    
    private static ImageRectifier rectifier = null;
	
    
    static IplImage defC;
    static IplImage defD;
    
    
    public CoinGUI(){
  
    }
    
	public static void main(String[] args) {
		final Window w = new Window(new Dimension(1350,750));
		
		final KinectReader kr = new KinectReader();
		
		defC = cvLoadImage("test_images/trialcount_img.png");
		defD = cvLoadImage("test_images/trialcount_depth.png");
		
		mainI = kr.getColorFrame();
		currentI = defC;
		currentDI = defD;
		
		final CameraCalibrator cc = new CameraCalibrator();
		cc.boardSize = new CvSize(4,5);
		

		
		JButton run = new JButton("Run All");
		run.setMinimumSize(new Dimension(200,30));
		w.add(run,0,0,1,1,1,0);
		run.setBackground(Color.GREEN);
		
		JButton capc = new JButton("Snap Color!");
		capc.setMinimumSize(new Dimension(200,30));
		w.add(capc,1,0,1,1,1,0);
		capc.setBackground(Color.GREEN.darker());
		
		JButton capd = new JButton("Snap Depth!");
		capd.setMinimumSize(new Dimension(200,30));
		w.add(capd,2,0,1,1,1,0);
		capd.setBackground(Color.GREEN.darker());

		JButton Load = new JButton("Load");
		Load.setMinimumSize(new Dimension(200,30));
		w.add(Load,3,0,1,1,1,0);
		Load.setBackground(Color.GREEN.darker());
		
		JButton save = new JButton("Save");
		save.setMinimumSize(new Dimension(200,30));
		w.add(save,4,0,1,1,1,0);
		save.setBackground(Color.GREEN.darker());

		JButton exit = new JButton("Exit");
		exit.setMinimumSize(new Dimension(200,30));
		w.add(exit,5,0,1,1,1,0);
		exit.setBackground(Color.GREEN.darker());
		
		JButton black = new JButton("Set Black");
		black.setMinimumSize(new Dimension(200,30));
		w.add(black,0,1,1,1,0,0);
		black.setBackground(Color.BLUE);
		
		JButton colcal = new JButton("Calibrate Color");
		colcal.setMinimumSize(new Dimension(200,30));
		w.add(colcal,1,1,1,1,0,0);
		colcal.setBackground(Color.BLUE);
		
		JButton camcal = new JButton("Calibrate Camera");
		camcal.setMinimumSize(new Dimension(200,30));
		w.add(camcal,2,1,1,1,0,0);
		camcal.setBackground(Color.CYAN);
		
		JButton findchess = new JButton("Find CheckerBoard");
		findchess.setMinimumSize(new Dimension(200,30));
		w.add(findchess,3,1,1,1,0,0);
		findchess.setBackground(Color.CYAN);
		
		JButton getdepth = new JButton("Pick Depth Data");
		getdepth.setMinimumSize(new Dimension(200,30));
		w.add(getdepth,4,1,1,1,0,0);
		getdepth.setBackground(Color.ORANGE);
		
		JButton getcoins = new JButton("Run Coin Finder");
		getcoins.setMinimumSize(new Dimension(200,30));
		w.add(getcoins,5,1,1,1,0,0);
		getcoins.setBackground(Color.ORANGE);
		
		JButton findcoins = new JButton("Find Coins");
		findcoins.setMinimumSize(new Dimension(200,30));
		w.add(findcoins,0,2,1,1,0,0);
		findcoins.setBackground(Color.ORANGE);
		
		JButton rectify = new JButton("Rectify");
		rectify.setMinimumSize(new Dimension(200,30));
		w.add(rectify,1,2,1,1,0,0);
		rectify.setBackground(Color.ORANGE);
		
		JButton rrectify = new JButton("Reverse Rectify");
		rrectify.setMinimumSize(new Dimension(200,30));
		w.add(rrectify,2,2,1,1,0,0);
		rrectify.setBackground(Color.ORANGE);
	
		JButton remap = new JButton("Remap from Calibration");
		remap.setMinimumSize(new Dimension(200,30));
		w.add(remap,3,2,1,1,0,0);
		remap.setBackground(Color.CYAN);
	
		JButton findAxis = new JButton("Find Axis");
		findAxis.setMinimumSize(new Dimension(200,30));
		w.add(findAxis,4,2,1,1,0,0);
		findAxis.setBackground(Color.magenta);
	
	    final JPanel mainP = w.ImagePanel(mainI, 1);
	    final JPanel currentP = w.ImagePanel(currentI, 1);
		    
	    
	    w.add(mainP,0,4,3,1,1,1);
	    w.add(currentP,3,4,3,1,1,1);
	    
	    w.add(samples,0,5,6,1,1,1);
	    
	    w.add(con,0,6,6,1,1,1);
		con.addln("Here We GO!!!");
	    
		
	    final DepthPicker depthPicker = new DepthPicker();
        depthPicker.addWindowListener(
        		new WindowAdapter() { 
        			public void windowClosing(WindowEvent e) { 
        				w.setEnabled(true);
        				depthPicker.setVisible(false);
        				depthPickerData = depthPicker.getCoords();
        				if (depthPickerData != null) {
        					for (Integer coord : depthPickerData) {
        						con.add(coord.toString()+", ");
        				    }
        				    con.newln();
        				    
        				    //rectifyImage();
        				    //samples.removeAll();
                    		//samples.add(w.ImagePanel(trialTable, 2));
                    		//samples.add(w.ImagePanel(colorImage, 2));
                    		//samples.add(w.ImagePanel(rectifiedImage, 2));
                      		//samples.add(w.ImagePanel(depthImage, 2));
                      		//samples.add(w.ImagePanel(rectifiedDepth, 2));
                      		//currentI = rectifiedImage;
        				    
        			    } else {
        			    	con.add("Bad selection.\n");
        			    }
        }});

	    capc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	con.addln("Capturing Images! (Showing Color)");
            	currentI = kr.getColorFrame();
            	currentDI = kr.getDepthFrame();
            	w.ImagePanelUpdate(currentP, currentI, 1);
            }});   

	    capd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	con.addln("Capturing Images! (Showing Depth)");
            	currentI = kr.getColorFrame();
            	currentDI = kr.getDepthFrame();
            	w.ImagePanelUpdate(currentP, currentDI , 1);
            }});   
	    
	    save.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e){
	            	ImageConverter ic = new ImageConverter();
	            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
	            	fc.showSaveDialog(new JFrame());
	            	String path = fc.getSelectedFile().getAbsolutePath();
	            	con.addln("Saving Image to: "+path);
	            	ic.savePNG(path.substring(0, path.length()-4), currentI);
	            }});     
	        
	    Load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
            	fc.showOpenDialog(new JFrame());
            	String path = fc.getSelectedFile().getAbsolutePath();
            	con.addln("Loading Image From: "+path);
            	currentI = cvLoadImage(path);
            	if(currentI.height() > w.getHeight()-500){
            		currentI = w.scale(currentI, 2);
            	}
            	w.ImagePanelUpdate(currentP, currentI, 1);
            }});   
    
	    exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	w.exit();
            }});   
    
	    black.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
              	BlackBalance blackBal = new BlackBalance(currentI);
            	BLACK = blackBal.getHsvValues();
            	con.addln("Black Set.");
            }});   

	    colcal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
        		
        		if (BLACK != null) {
        			samples.removeAll();
            		samples.add(w.ImagePanel(currentI, 2));
	        		ColorChart chart = new ColorChart(currentI, BLACK);
	        		if (! chart.findCalibColors()) {
	        			con.addln("Cannot find colors!");
	        			//System.out.println("Cannot find colors!");
	        		} else {
	        			samples.add(w.ImagePanel(chart.getGoldImg(), 2));
	        			samples.add(w.ImagePanel(chart.getSilverImg(), 2));
		        		con.addln(chart.getColorData());
	        		}
	        		w.revalidate();
        		}
        		else {
        			con.addln("Set black first!");
        		}
            }});   

	    
	    
	    camcal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	//currentI = kr.getColorFrame();
            	
             	 Resolution = cc.Resolution;
            	 
            	
            	if(cc.SampleAt == 0){
            		samples.removeAll();
            	}
            	
            	if(cc.SampleAt < cc.Samples){
	            	if(cc.Samples == cc.SampleAt){
	            		con.addln("Sample Not Added. Sample Limit Reached!");
	            	}else{
	            		boolean has = cc.addToCalibration(currentI);
		            	if(has == true){
		            		
		            		samples.add(w.ImagePanel(currentI, 4));
		            		con.addln("Calibration Image added at "+cc.SampleAt+"/"+cc.Samples);
		            		objectPoints = cc.objectPoints;
		            		imagePoints = cc.imagePoints;

		            		//samples.add(new JLabel(new ImageIcon(resize(mainimg.clone().getBufferedImage(),mainimg.width()/4,mainimg.height()/4))),gc);
		            		//revalidate();
		            		w.ImagePanelUpdate(currentP, currentI, 1);
		            	}else{
		            		con.addln("Sample Not Added. No Board Found!");
		            	}
	            	}
	            	
            	
            	}else{
	            	con.add("Performing Calibration on "+cc.Samples+" Samples...");
	            	error = cc.calibrate();
	             	 mapx = cc.mapx;
	             	 mapy = cc.mapy;
	             	 cameraMatrix = cc.cameraMatrix;	
	             	 distCoeffs = cc.distCoeffs;
	             	 rotVectors = cc.rotVectors;
	             	 transVectors = cc.transVectors;
	             	 
	             	 System.out.print(rotVectors.toString());
	             	 System.out.print(transVectors.toString());


	            	con.addln("Error = "+error);
	            	cc.SampleAt = 0;
            	}
            }});   

	    
	    findchess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
             	con.add("Looking for "+cc.boardSize.toString()+" Chess Board... ");
            	IplImage im = cc.FindChessboard(currentI);
            	if(cc.patternFound > 0){
            		con.add(cc.patternFound+" Pattern(s) Found!");
            		con.newln();
            		w.ImagePanelUpdate(currentP, im, 1);
            	}else{
            		con.add("No Chessboard Found!");
            		con.newln();
            	}
            }});   
    
	    getdepth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            //	IplImage overlayed = null;
        	//	if (currentDI.width() == currentI.width()) {
    	    //		cvAddWeighted(currentI, 1.0, currentDI, 0.5, 0.0, overlayed);
    	    //		w.ImagePanelUpdate(mainP, overlayed, 1);
        	//	}
 
            	depthPicker.setImage(currentDI);
            	depthPicker.setVisible(true);
            	
            	con.add("Pick a region...\n");
            	w.setEnabled(false);
            	
            	// RECTIFICATION INITIATED ON WINDOW CLOSE
            }});   
    
	    getcoins.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	if (rectifiedImage == null) {
            		samples.removeAll();
            		con.add("Finding coins from captured image...");
            		IplImage threechannel = cvCreateImage(cvSize(currentI.width(), currentI.height()), IPL_DEPTH_8U, 3);
            	    cvCvtColor(currentI, threechannel, CV_RGBA2RGB);
            	    
            		CoinFinder coinFinder = new CoinFinder(threechannel, currentDI);
	            	coinFinder.find();
	            	IplImage drawnCoins = coinFinder.getDrawnCoins();
	            	samples.add(w.ImagePanel(drawnCoins, 4));

            	} else {
            		con.add("Finding coins from rectified image...");
	            	CoinFinder coinFinder = new CoinFinder(rectifiedImage, depthImage);
	            	coinFinder.find();
	            	IplImage drawnCoins = coinFinder.getDrawnCoins();
	            	samples.add(w.ImagePanel(drawnCoins, 2));
	            	IplImage reverseRect = reverseRectify(drawnCoins);
	            	samples.add(w.ImagePanel(reverseRect, 2));
	            	
	            	// ASSUMES THAT A WHITE PLATE IS BEING USED!
            	}
            }});   
    
	    rectify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
			    rectifyImage();
			    samples.removeAll();
        		samples.add(w.ImagePanel(trialTable, 4));
        		samples.add(w.ImagePanel(colorImage, 4));
        		samples.add(w.ImagePanel(rectifiedImage, 4));
          		samples.add(w.ImagePanel(depthImage, 4));
          		samples.add(w.ImagePanel(rectifiedDepth, 4));
          		currentI = rectifiedImage;
          		currentDI = rectifiedDepth;
          		w.ImagePanelUpdate(currentP, currentI, 1);
            }});   
	    
	    rrectify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	currentI = reverseRectify(currentI);
            	currentDI = reverseRectify(currentDI);
            	samples.add(w.ImagePanel(currentI, 4));
            	samples.add(w.ImagePanel(currentDI, 4));
            	w.ImagePanelUpdate(currentP, currentI, 1);
            }});   
	    
	    findcoins.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            		con.add("Finding coins from captured image...");
            		IplImage threechannel = cvCreateImage(cvSize(currentI.width(), currentI.height()), IPL_DEPTH_8U, 3);
            	    cvCvtColor(currentI, threechannel, CV_RGBA2RGB);
            	    
            		CoinFinder coinFinder = new CoinFinder(threechannel, currentDI);
	            	coinFinder.find();
	            	IplImage drawnCoins = coinFinder.getDrawnCoins();
	            	samples.add(w.ImagePanel(drawnCoins, 4));
	              	w.ImagePanelUpdate(currentP, drawnCoins, 1);
            }});   

	    remap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	currentI = cc.remap(currentI);
            	currentDI = cc.remap(currentDI);
              	w.ImagePanelUpdate(currentP, currentI, 1);
            }});   
	    
	    findAxis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	AxisLocator al = new AxisLocator(currentI);
            	al.findAxis(currentI);
            }});   
	    
	    w.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				w.exit();
			}
    	});
	    
		

		while(true){
			IplImage overlayed = kr.getDepthFrame();
			IplImage depthframe = kr.getDepthFrame();
			IplImage colorframe = kr.getColorFrame();
			
			mainI = colorframe;
			
			//AxisLocator al = new AxisLocator(colorframe);
		//	al.findAxis(colorframe);
			
    		if (depthframe.width() == colorframe.width()) {
	    		cvAddWeighted(colorframe, 1.0, depthframe, 0.5, 0.0, overlayed);
	    		w.ImagePanelUpdate(mainP, overlayed, 1);
    		}
    		
        	
		}
	}
	
	   private static void rectifyImage() {
	    	if (depthPickerData.isEmpty()) {
	    		con.add("Pick a region first!\n");
	    	} else {
	    		colorImage = currentI;
	    		depthImage = currentDI;
	    		
	    		rectifier = new ImageRectifier(currentI, currentDI, depthPickerData);
	    		trialTable = rectifier.drawTableLines();
	    		con.add(rectifier.getDepthData().toString());
	    		rectifiedImage = rectifier.transformImage();
	    		rectifiedDepth = rectifier.transformDepthImage();
	    	}
	    }
	    
	    private static IplImage reverseRectify(IplImage totrans) {
	    	IplImage reversed = null;
	    	if (rectifier == null) {
	    		con.add("Cannot reverse without initial rectification.\n");
	    	} else {
	    		reversed = rectifier.reverseTransform(totrans);
	    	}
	    	return reversed;
	    }
	    
	    private void visualiseExtrinsics(){
	    	//TODO use rotVectors and transVectors to output a map of camera positions relating to the board.
	    	CvMat rotOut = CvMat.create(rotVectors.length(),3);
	    	CvMat rot = new CvMat();
	    	rot.put(rotVectors.get(0,0));
	    	rot.put(rotVectors.get(1,0));
	    	rot.put(rotVectors.get(2,0));
	    	cvRodrigues2(rot, rotOut,new CvMat());
	    	
	    }
	    
	    

	public void exit(){
		System.exit(0);
	}

	
}
