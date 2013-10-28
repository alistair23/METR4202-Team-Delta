package gui;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_UNCHANGED;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvRodrigues2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import localisation.AxisLocator;
import colorCalibration.BlackBalance;
import colorCalibration.BlobFinder;
import colorCalibration.ColorChart;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import cameraCalibration.CameraCalibrator;
import capture.*;
import functions.*;

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
	static IplImage currentI;

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
    
    //default images
    static IplImage defC; //Default color image  
    
    //capture and image objects
    static CameraReader kr;
     
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
		kr = new CameraReader();
		boolean haveKinect = kr.Start();
		
		//make a new window.
		final Window w = new Window(new Dimension(1350,750));
		w.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		//set default images
		defC = cvLoadImage("system_images/noCam.png");
				
		mainI = defC;		
		currentI = defC;
		
		//set up a new camera calibrator.
		final CameraCalibrator cc = new CameraCalibrator();
		cc.boardSize = new CvSize(5,4);
		cc.Samples=numsamples;
		cc.setup();

		//define all the gui buttons and add them to the GUI
		
		final JButton capc = new JButton("Snap Color!");
		capc.setMinimumSize(new Dimension(200,30));
		w.add(capc,0,0,1,1,1,0);
		capc.setBackground(Color.GREEN.darker());

		JButton Load = new JButton("Load");
		Load.setMinimumSize(new Dimension(200,30));
		w.add(Load,1,0,1,1,1,0);
		Load.setBackground(Color.GREEN.darker());
		
		JButton save = new JButton("Save");
		save.setMinimumSize(new Dimension(200,30));
		w.add(save,2,0,1,1,1,0);
		save.setBackground(Color.GREEN.darker());
		
		JButton colcal = new JButton("Calibrate Color");
		colcal.setMinimumSize(new Dimension(200,30));
		w.add(colcal,3,0,1,1,1,0);
		colcal.setBackground(Color.BLUE.brighter());
		
		JButton camcal = new JButton("Calibrate Camera");
		camcal.setMinimumSize(new Dimension(200,30));
		w.add(camcal,0,1,1,1,1,0);
		camcal.setBackground(Color.CYAN);
		
		JButton getcoins = new JButton("Run Coin Finder");
		getcoins.setMinimumSize(new Dimension(200,30));
		w.add(getcoins,1,1,1,1,1,0);
		getcoins.setBackground(Color.ORANGE);
	
		JButton remap = new JButton("Remap from Calibration");
		remap.setMinimumSize(new Dimension(200,30));
		w.add(remap,2,1,1,1,1,0);
		remap.setBackground(Color.CYAN);
	
		JButton findAxis = new JButton("Find Axis");
		findAxis.setMinimumSize(new Dimension(200,30));
		w.add(findAxis,3,1,1,1,1,0);
		findAxis.setBackground(Color.magenta);
	
	    final JPanel mainP = w.ImagePanel(mainI, 1);
	    final JPanel currentP = w.ImagePanel(currentI, 1);
		
	    w.add(mainP,0,2,2,1,1,1);
	    w.add(currentP,2,2,2,1,1,1);
	    
	    w.add(samples,0,3,4,1,1,1);
	    
	    w.add(con,0,4,6,1,1,1);
		con.addln("Here We GO!!!");

		//disable buttons if there is no camera
		if(haveKinect){
			mainI = kr.getColorFrame();
			currentI = mainI;
			w.ImagePanelUpdate(currentP, currentI, 1);
		}else{
			capc.setEnabled(false);
			camcal.setEnabled(false);
		}
		
		//define button actions
		capc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	con.addln("Capturing Images! (Showing Color)");
            	currentI = kr.getColorFrame();
            	w.ImagePanelUpdate(currentP, currentI, 1);
            }});
	        
	    Load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
            	fc.showOpenDialog(new JFrame());
            	String path = fc.getSelectedFile().getAbsolutePath();
            	con.addln("Loading Image From: "+path);
            	currentI = cvLoadImage(path);
            	if(currentI.height() > w.getHeight()-500){
            		currentI = w.scale(currentI, 1);
            	}
            	w.ImagePanelUpdate(currentP, currentI, 1);
            }});
	    
	    save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
            	fc.showSaveDialog(new JFrame());
            	String path = fc.getSelectedFile().getAbsolutePath();
            	con.addln("Saving Image to: "+path+".png");
            	
            	File outputfile = new File(path.substring(0, path.length())+".png");
        		try {
        			ImageIO.write(currentI.getBufferedImage(), "png", outputfile);
        		} catch (IOException ex) {}
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
    
	    getcoins.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            		samples.removeAll();
            		con.addln("Finding coins in image...");
	            	CoinFinder coinFinder = new CoinFinder(currentI, 260.0);
	            	coinFinder.find();
	            	IplImage drawnCoins = coinFinder.getDrawnCoins();
	            	//samples.add(w.ImagePanel(drawnCoins, 2));
	            	w.ImagePanelUpdate(currentP, drawnCoins, 1);
	            	coinFinder.determineValues();
	            	con.add(coinFinder.getValues().toString());
            	}
            });

	    remap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	currentI = cc.remap(currentI);
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
				
				kr.Stop();
				w.exit();
				System.exit(0);
				
			}});
	    
	    //update the main window with the camera feed forever
	    
	    String baseString = "training_images/20/guy.png";
		IplImage baseImage = cvLoadImage(baseString);
		w.ImagePanelUpdate(currentP, baseImage, 1);
		Sifter sifter = new Sifter(baseImage);
		double height = 380.0;
		while(true){
			if(haveKinect){
				mainI = kr.getColorFrame();
		    	w.ImagePanelUpdate(mainP, mainI, 1);
		    	
		    	/**
		    	//w.ImagePanelUpdate(currentP, findCoins(mainI), 1);
		    	cvErode(mainI, mainI, null, 3);
		  	  	cvDilate(mainI, mainI, null, 3);
		  	  	
		    	CoinFinder coinFinder = new CoinFinder(mainI, height);
		    	coinFinder.find();
	        	coinFinder.determineValues();
	        	IplImage drawnCoins = coinFinder.getDrawnCoins();
	        	OpticalFlowTracker flowTracker = new OpticalFlowTracker();
		    	IplImage trackedImage = flowTracker.trackMovement(drawnCoins, kr.getColorFrame());
		    	w.ImagePanelUpdate(currentP, trackedImage, 1);
		    	con.wipe();
				con.addln(coinFinder.getValues().toString());
				*/
		    	
		    	//BlobFinder blob = new BlobFinder(mainI);
		    	//CvScalar min = new CvScalar(130, 0, 50, 0);
		    	//CvScalar max = new CvScalar(200, 255, 255, 0);
		    	//IplImage blobImage = blob.findBlobs(mainI, min, max, 8000);
		    	
				/**
				IplImage imgHSV = cvCreateImage(cvGetSize(mainI), 8, 3);
				cvCvtColor(mainI, imgHSV, CV_BGR2HSV);
				IplImage imgThreshold = cvCreateImage(cvGetSize(mainI), 8, 1);
				cvInRangeS(imgHSV, min, max, imgThreshold);
				cvReleaseImage(imgHSV);
				cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 1);
				*/
		    	
		    	//w.ImagePanelUpdate(currentP, blobImage, 1);
				
		    	
				sifter.sift(mainI);
				con.addln("Count: "+sifter.getMatchCount().toString());
				con.addln("Distance: "+sifter.getDistance().toString());
				
				//IplImage drawMatches = sifter.drawMatchesOnImage(mainI);
				IplImage drawMatches = sifter.drawMatchPoints(mainI);
				w.ImagePanelUpdate(currentP, drawMatches, 1);
				
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
	    
	    // UNRELIABLE : NOT FULLY IMPLEMENTED
	    private void visualiseExtrinsics(){
	    	
	    	CvMat rotOut = CvMat.create(rotVectors.length(),3);
	    	CvMat rot = new CvMat();
	    	rot.put(rotVectors.get(0,0));
	    	rot.put(rotVectors.get(1,0));
	    	rot.put(rotVectors.get(2,0));
	    	cvRodrigues2(rot, rotOut,new CvMat());
	    }
}
