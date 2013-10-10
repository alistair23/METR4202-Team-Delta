package gui;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvRodrigues2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
import capture.OverlayImage;
import functions.CoinFinder;
import functions.ImageRectifier;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * Main user interface.
 * This class interacts with other classes to visualise the results
 * of the coin sensing and localisation process.
 * 
 * Color and camera calibration are not necessary for coin finding (but may improve results).
 * 
 * MUST run in order of:
 * 1) Snap color image
 * 2) Find axis
 * 3) Rectify image
 * 4) Find coins
 * 
 */

public class CoinGUI extends JFrame{


	
	//define main images
	static IplImage mainI;
	static IplImage mainDI;
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
	
 	//axis location matrix
 	private static CvMat axisMatrix = null;
 	
	//Color Calibration
	private static CvScalar BLACK = null;
	
	//Depth Picker and coin finder
    public static ArrayList<Integer> depthPickerData = new ArrayList<Integer>();
    private static IplImage colorImage;
    private static IplImage depthImage;
    private static IplImage rectifiedDepth = null;
    private static IplImage rectifiedImage = null;
    private static IplImage trialTable = null;
    
    //default images
    static IplImage defC; //Default color image  
    static IplImage defD; //default depth image
    
    //capture and image objects
    static KinectReader kr;
    static OverlayImage overlay;
    static ImageRectifier rectifier;
     
    //gui elements
	static ConsolePanel con = new ConsolePanel();
	static JPanel samples = new JPanel();

    //tweaking constants
 	static int wait = 300;
 	static int numsamples = 20;
    
    public CoinGUI(){
  
    }
    
	public static void main(String[] args) {
		
		//start the camera. if it isn't found, the havekinect variable is false.
		kr = new KinectReader();
		boolean haveKinect = kr.Start();
		
		//make a new window.
		final Window w = new Window(new Dimension(1350,750));
		w.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		//set default images
		defC = cvLoadImage("system_images/noCam.png");
		defD = cvLoadImage("system_images/noCam.png");
				
		mainI = defC;
		mainDI = defD;
		
		currentI = defC;
		currentDI = defD;
		
		//set up a new camera calibrator.
		final CameraCalibrator cc = new CameraCalibrator();
		cc.boardSize = new CvSize(5,4);
		cc.Samples=numsamples;
		cc.setup();

		//define all the gui buttons and add them to the GUI
		JButton run = new JButton("Run All");
		run.setMinimumSize(new Dimension(200,30));
		w.add(run,0,0,1,1,1,0);
		run.setBackground(Color.GREEN);
		
		final JButton capc = new JButton("Snap Color!");
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
		
		JButton colcal = new JButton("Calibrate Color");
		colcal.setMinimumSize(new Dimension(200,30));
		w.add(colcal,0,2,1,1,0,0);
		colcal.setBackground(Color.BLUE.brighter());
		
		JButton camcal = new JButton("Calibrate Camera");
		camcal.setMinimumSize(new Dimension(200,30));
		w.add(camcal,1,1,1,1,0,0);
		camcal.setBackground(Color.CYAN);
		
		JButton findchess = new JButton("Find CheckerBoard");
		findchess.setMinimumSize(new Dimension(200,30));
		w.add(findchess,0,1,1,1,0,0);
		findchess.setBackground(Color.CYAN.darker());
		
		JButton getcoins = new JButton("Run Coin Finder");
		getcoins.setMinimumSize(new Dimension(200,30));
		w.add(getcoins,3,1,1,1,0,0);
		getcoins.setBackground(Color.ORANGE);
		
		JButton rectify = new JButton("Rectify");
		rectify.setMinimumSize(new Dimension(200,30));
		w.add(rectify,4,1,1,1,0,0);
		rectify.setBackground(Color.ORANGE.darker());
		
		JButton rrectify = new JButton("Reverse Rectify");
		rrectify.setMinimumSize(new Dimension(200,30));
		w.add(rrectify,5,1,1,1,0,0);
		rrectify.setBackground(Color.ORANGE.darker());
	
		JButton remap = new JButton("Remap from Calibration");
		remap.setMinimumSize(new Dimension(200,30));
		w.add(remap,2,1,1,1,0,0);
		remap.setBackground(Color.CYAN);
	
		JButton findAxis = new JButton("Find Axis");
		findAxis.setMinimumSize(new Dimension(200,30));
		w.add(findAxis,5,2,1,1,0,0);
		findAxis.setBackground(Color.magenta);
	
	    final JPanel mainP = w.ImagePanel(mainI, 1);
	    final JPanel currentP = w.ImagePanel(currentI, 1);
		
	    w.add(mainP,0,4,3,1,1,1);
	    w.add(currentP,3,4,3,1,1,1);
	    
	    w.add(samples,0,5,6,1,1,1);
	    
	    w.add(con,0,6,6,1,1,1);
		con.addln("Here We GO!!!");

		//disable buttons if there is no camera
		if(haveKinect){
			mainI = kr.getColorFrame();
			mainDI = kr.getDepthFrame();
			currentI = mainI;
			currentDI = mainDI;
			w.ImagePanelUpdate(currentP, currentI, 1);
		}else{
			capc.setEnabled(false);
			capd.setEnabled(false);
			camcal.setEnabled(false);
		}
		
		//define button actions
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
	            	con.addln("Saving Image to: "+path+".png");
	            	ic.savePNG(path.substring(0, path.length()), currentI);
	            	ic.savePNG(path.substring(0, path.length())+"-Depth", currentDI);
	            }});     
	        
	    Load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
            	fc.showOpenDialog(new JFrame());
            	String path = fc.getSelectedFile().getAbsolutePath();
            	con.addln("Loading Image From: "+path);
            	currentI = cvLoadImage(path);
            	currentDI = cvLoadImage(path.substring(0, path.length()-4)+"-Depth.png");
            	if(currentI.height() > w.getHeight()-500){
            		currentI = w.scale(currentI, 1);
            	}
            	w.ImagePanelUpdate(currentP, currentI, 1);
            }});   
    
	    exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	w.exit();
            }});

	    colcal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
        		capc.doClick();
        		
            	if (BLACK == null) {
            		IplImage blkimg = cvLoadImage("test_images/black.png");
            		BlackBalance blackBal = new BlackBalance(blkimg);
                	BLACK = blackBal.getHsvValues();
            	}
            	
        			samples.removeAll();
            		samples.add(w.ImagePanel(currentI, 2));
	        		ColorChart chart = new ColorChart(currentI, BLACK);
	        		if (! chart.findCalibColors()) {
	        			con.addln("Cannot find colors!");
	        		} else {
	        			samples.add(w.ImagePanel(chart.getGoldImg(), 2));
	        			samples.add(w.ImagePanel(chart.getSilverImg(), 2));
		        		con.addln(chart.getColorData());
	        		}
	        		w.revalidate();
            }});
	    
	    camcal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
           	while(cc.SampleAt < cc.Samples) {
            	
           		try {
           		    Thread.sleep(wait);
           		} catch(InterruptedException ex) {
           		    Thread.currentThread().interrupt();
           		}
           		
            	currentI = kr.getColorFrame();
            	w.ImagePanelUpdate(currentP, currentI, 1);
            	
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

		            		w.ImagePanelUpdate(currentP, currentI, 1);
		            	}else{
		            		con.addln("Sample Not Added. No Board Found!");
		            	}
	            	}
	            	
            	
            	}else{
	            	con.addln("Performing Calibration on "+cc.Samples+" Samples...");
	             	 cc.calibrate();
	            	con.addln("Error = "+cc.error);
	            	con.add("Focal X: "+cc.fx);
	            	con.addln(" | Focal Y: "+cc.fy);
	            	con.add("center X: "+cc.cx);
	            	con.addln(" | center Y: "+cc.cy);
	            	con.add("radial dist 1: "+cc.k1);
	            	con.add(" | radial dist 2: "+cc.k2);
	            	con.addln(" | radial dist 3: "+cc.k3);
	            	con.add("tangentail dist 1: "+cc.p1);
	            	con.addln(" | tangentail dist 2: "+cc.p2);
	            	
	            	cc.SampleAt = 0;
            	}
            	}
           	
				con.addln("Performing Calibration on "+cc.Samples+" Samples...");
				cc.calibrate();
				con.addln("Error = "+cc.error);
				con.add("Focal X: "+cc.fx);
				con.addln(" | Focal Y: "+cc.fy);
				con.add("center X: "+cc.cx);
				con.addln(" | center Y: "+cc.cy);
				con.add("radial dist 1: "+cc.k1);
				con.add(" | radial dist 2: "+cc.k2);
				con.addln(" | radial dist 3: "+cc.k3);
				con.add("tangentail dist 1: "+cc.p1);
				con.addln(" | tangentail dist 2: "+cc.p2);
				cc.SampleAt = 0;
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
    
	    getcoins.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	if (rectifiedImage == null) {
            		System.out.println("Only implemented for rectified image!");
            		
            	} else {
            		con.addln("Finding coins from rectified image...");
	            	CoinFinder coinFinder = new CoinFinder(rectifiedImage, depthImage, rectifier.getMatrix());
	            	
	            	con.add("Coin rotation matrix: ");
	            	con.addln(rectifier.getMatrix().toString());
	            	con.newln();
	            	
	            	coinFinder.find();
	            	IplImage drawnCoins = coinFinder.getDrawnCoins();
	            	samples.add(w.ImagePanel(drawnCoins, 4));
	            	
	            	IplImage reverseRect = reverseRectify(drawnCoins);
	            	w.ImagePanelUpdate(currentP, reverseRect, 1);
	            	coinFinder.determineValues();
	            	con.add("{5c, 10c, 20c, 50c, 1aud, 2aud} : ");
	            	con.addln(coinFinder.getValues().toString());
	            	con.addln("Total value: $"+coinFinder.getTotalValue().toString());
	            	con.newln();
	            	
	            	double relx = axisMatrix.get(0, 3)*1000, rely = axisMatrix.get(1, 3)*1000, relz = axisMatrix.get(2, 3)*1000;
	            	
	            	// OUTPUTS COORDINATES RELATIVE TO PLACED REFERENCE
	            	// VALUES MAY BE UNRELIABLE... SOME BUGS REMAIN
	            	for (TreeMap<Double, ArrayList<Double>> thismap : coinFinder.getCoinLocationData()) {
	            		Double value = thismap.firstKey();
	            		
	            		ArrayList<Double> trans = thismap.get(value);
	            		double tx = trans.get(0), ty = trans.get(1), tz = trans.get(2);
	            		
	            		CvMat datmat = cvCreateMat(4,1,axisMatrix.type());
	            		datmat.put(0, -tx); datmat.put(1, ty);
	            		datmat.put(2, tz); datmat.put(3, 0.0);
	            		
	            		CvMat outmat = cvCreateMat(4,1,axisMatrix.type());
	            		outmat.put(0, 0.0); outmat.put(1, 0.0);
	            		outmat.put(2, 0.0); outmat.put(3, 0.0);
	            		
	            		cvMatMul(axisMatrix, datmat, outmat);
	            		outmat.put(0, outmat.get(0)-tx);
	            		outmat.put(1, outmat.get(1)+ty);
	            		outmat.put(2, outmat.get(2)+tz);
	            		
	            		con.add("Value: "+value+",  Coords: ");
	            		con.addln(outmat.toString());
	            	}
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

	    remap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	currentI = cc.remap(currentI);
            	currentDI = cc.remap(currentDI);
              	w.ImagePanelUpdate(currentP, currentI, 1);
            }});   
	    
	    findAxis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	AxisLocator al = new AxisLocator(currentI);
            	axisMatrix = al.findAxis(currentI);
            	if (axisMatrix != null) {
            		con.add("Axis Matrix:   ");
            		con.addln(axisMatrix.toString());
            		con.newln();
            	} else {
            		con.add("No marker found!\n");
            	}
            }});   
	    
	    w.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				w.exit();
			}});
	    
	    //update the main window with the camera feed forever
		while(true){
			if(haveKinect){
				mainI = kr.getColorFrame();
				mainDI = kr.getDepthFrame();
				
				//overlay the color image with the depth 
				IplImage overlay = mainI;
				cvAddWeighted(mainI, 1.0, mainDI, 0.5, 0.0, overlay);
		    	w.ImagePanelUpdate(mainP, overlay, 1);
			}
		}
		
	//	Attempting to implement threading
		//    videoPanel v = new videoPanel(kr);
		//	    mainP.removeAll();
		//	    mainP.add(v);
		//	   Thread thread = (new Thread(v));
		//	   System.out.println(thread.getId());
		//	   thread.start();
		
		
	}
	
	   private static void rectifyImage() {
	    		colorImage = currentI;
	    		depthImage = currentDI;
	    		
	    		rectifier = new ImageRectifier(currentI, currentDI);
	    		trialTable = rectifier.drawTableLines();

	    		rectifiedImage = rectifier.transformImage();
	    		rectifiedDepth = rectifier.transformDepthImage();
	    }
	    
	    private static IplImage reverseRectify(IplImage totrans) {
	    	IplImage reversed = null;
	    	if (rectifier == null) {
	    		con.addln("Cannot reverse without initial rectification.");
	    	} else {
	    		reversed = rectifier.reverseTransform(totrans);
	    	}
	    	return reversed;
	    }
	    
	    // UNRELIABLE : NOT FULLY IMPLEMENTED
	    private void visualiseExtrinsics(){
	    	
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
