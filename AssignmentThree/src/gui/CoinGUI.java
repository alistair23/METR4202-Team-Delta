package gui;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import localisation.AxisLocator;
import actuation.ArmControl;
import colorCalibration.BlackBalance;
import colorCalibration.ColorChart;
import cameraCalibration.CameraCalibrator;
import capture.*;
import functions.*;

/**
 * @author Ben Merange, Ben Rose, Alistair Francis, Bob Zhou and Clinton Walker
 *	
 *	The main GUI for operation; the interface with the user for all other classes.
 *	Intended for use with any webcam and a specificly designed Dynamixel robotic arm.
 *	
 *	Identifies Australian currency notes and coins, tracking their movement based off a Kanji marker
 *	placed in the middle of the rotating turntable. Communicates with a custom Dynamixel robitic arm
 *	via serial to actuate as tracked coins are reachable. The arm utilizes a vacuum tool tip to move
 *	coins off the table to their appropriate box (big gold, small gold, big silver, small silver).
 *	The turntable speed is fully adjustable by the user (as long as it remains constant during
 *	actuation of the arm).
 *
 *	Calibration features from the previous project (see Assignment 2 on this Git) have been included:
 *	camera calibration (intrinsics and extrinsics) and color calibration via color chart.
 *	
 *	Actuation for notes is unimplemented in this version, although it identical to the coin actuation
 *	code block but iterating instead over notesPolar.
 *
 */

public class CoinGUI extends JFrame{
	
	// TURN INITIAL NOTE SIFT ON OR OFF
	static boolean SIFTING = false;
	
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
     
    // GUI elements
	static ConsolePanel con = new ConsolePanel();
	static JPanel samples = new JPanel();

    // camera calibration constants
 	static int wait = 300;
 	static int numsamples = 20;
 	
 	static Double originXmm = null;
 	static Double originYmm = null;
 	
 	static ArrayList<TreeMap<String, ArrayList<Double>>> notesPolar = new ArrayList<TreeMap<String, ArrayList<Double>>>();
 	static CvFont font = new CvFont(CV_FONT_HERSHEY_PLAIN, 1, 1);
 	static double pixelSize = 0.0;
 	static CoinFinder coinFinder;
 	
	static ArrayList<TreeMap<Double, ArrayList<Double>>> coinsPolar = new ArrayList<TreeMap<Double, ArrayList<Double>>>();
	
	static double initTime = 0;
	static Double currentVelocity = 0.0;
 	
	static ArmControl arm = new ArmControl();
	static Thread armThread = new Thread(arm, "armThread");
	static double movingCoinTime = 1000;
	
	static ArrayList<Double> velHistory = new ArrayList<Double>();
	
    public CoinGUI(){}
    
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

		//define all the GUI buttons and add them to the GUI
		
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
		
		// define button actions
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
            	axisMatrix = al.findAxis();
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
	    
	   	// height of camera from tracking plane
	    double height = 375.0;
	    // only work if a camera is connected
	 	// although variable is named 'haveKinect' will work on most webcams
	    if (haveKinect) {
		    mainI = kr.getColorFrame();
		    coinFinder = new CoinFinder(mainI, height);
		    // pixelSize represents the mm/pixel ratio
		    pixelSize = coinFinder.getPixelSize();
		    
		    // set origin based on kaji marker
		    // keep updating until the kanji marker is identified
		    while (originXmm == null || originYmm == null) {
		    	mainI = kr.getColorFrame();
			    AxisLocator origin = new AxisLocator(mainI);
			    CvMat transMatrix = origin.findAxis();
			    if (transMatrix != null) {
			    	originXmm = -1000*transMatrix.get(0, 3);
				    originYmm = 1000*transMatrix.get(1, 3);
			    }
		    }
		    
		    // run initial sift on notes - run only if sift turned on
		    if (SIFTING) {
		    	con.addln("Finding notes...");
		    	// increase the sift threshold value if no matches, decrease if too many
		    	// corresponds to the feature match distance
		    	int SIFTTHRESHOLD = 195;
		    	File[] files = new File("training_images").listFiles();
		    	Sifter sifter = new Sifter(mainI, SIFTTHRESHOLD);
		    	ArrayList<String> labels = new ArrayList<String>();
		    	ArrayList<CvPoint> locations = new ArrayList<CvPoint>();
		    	
				for (File file : files) {
					String name = file.getName().substring(0, file.getName().length()-4);
					con.addln("Sifting: "+name);
					IplImage thisImage = cvLoadImage(file.toString());
					TreeSet<Integer> xValues = new TreeSet<Integer>();
					TreeSet<Integer> yValues = new TreeSet<Integer>();
					for (int i=0; i < 3; i++) {
						sifter = new Sifter(thisImage, SIFTTHRESHOLD);
						sifter.sift(kr.getColorFrame());
						for (CvPoint2D32f matchPoint : sifter.getGoodMatchPoints()) {
							xValues.add((int)matchPoint.x());
							yValues.add((int)matchPoint.y());
						}
					}
					// get median of all good points
					Integer[] xArray = xValues.toArray(new Integer[0]);
					Integer[] yArray = yValues.toArray(new Integer[0]);
					if (xArray.length > 3 && yArray.length > 3) {
						int x = xArray[(int) (((double)xArray.length)/2.0)];
						int y = yArray[(int) (((double)yArray.length)/2.0)];
					    CvPoint POINT = cvPointFrom32f(new CvPoint2D32f(x, y));
					    labels.add(name); locations.add(POINT);
					    Integer len = xArray.length;
					    con.addln(len.toString());
					    
					    // turn on for sift debugging - will show individual sift matches
				//	    IplImage debugImage = sifter.drawMatchPoints(kr.getColorFrame().clone());
				//	    cvShowImage("debug", debugImage);
				//	    cvWaitKey(0);
					}
				}
				// convert to polar coordinates, store in notesPolar and label on image
				IplImage pointsDrawn = kr.getColorFrame().clone();
				double time = (System.currentTimeMillis()/1000.0) - initTime;
				for (int i=0; i < labels.size(); i++) {
					String label = labels.get(i);
					CvPoint POINT = locations.get(i);
					cvPutText(pointsDrawn, label, POINT, font, CvScalar.GREEN);
					
		        	double offsetx = 320.0*pixelSize; double offsety = 240.0*pixelSize;
					double x = POINT.x()*pixelSize-offsetx; double y = offsety-POINT.y()*pixelSize;
	        		double diffx = x-originXmm; double diffy = y-originYmm;
	        		Double polarRadius = Math.sqrt(Math.pow(diffx, 2)+Math.pow(diffy, 2));
	        		Double polarAngleRad = Math.atan2(diffy, diffx);
					
					TreeMap<String, ArrayList<Double>> newmap = new TreeMap<String, ArrayList<Double>>();
	        		ArrayList<Double> polarcoords = new ArrayList<Double>();
	        		polarcoords.add(polarRadius); polarcoords.add(polarAngleRad); polarcoords.add(time);
	        		newmap.put(label, polarcoords);
	        		notesPolar.add(newmap);
				}
				w.ImagePanelUpdate(currentP, pointsDrawn, 1);
				pointsDrawn.release();
				con.addln("Notes found.");
		    }
	    }
	    
	    // set initial cariables and classes
	    initTime = System.currentTimeMillis()/1000.0;
	    AxisLocator origin = new AxisLocator(mainI);
	    double time = (System.currentTimeMillis()/1000.0) - initTime;
	    double z = 0.0;
	    boolean init = true;
	    
	    // MAIN LOOP - could implement this as functions and call from a new class to neaten things up
	    // will get around to it one day...
		while(true){
			// only work if a camera is connected
			// although variable is named 'haveKinect' will work on most webcams
			if(haveKinect){
				mainI = kr.getColorFrame();
		    	w.ImagePanelUpdate(mainP, mainI, 1);
		    	origin.setImage(mainI);
		    	
		    	// if cannot find the kanji marker stay in this loop
		    	while (origin.findAxis()==null) {
		    		// grab camera image
		    		mainI = kr.getColorFrame();
		    		// update left GUI image
			    	w.ImagePanelUpdate(mainP, mainI, 1);
			    	// set new image for AxisLocator class
			    	origin.setImage(mainI);
			    	
			    	// COPY COIN POSITION UPDATE AND OUTPUT IMAGE DRAWING INTO HERE
			    	// TO CONTINUE TO UPDATE THE COIN POSITIONS BASED ON LAST VELOCITY
		    	}
		    	
		    	// first time round skip velocity calculations and just update current values
		    	if (init == true) {
		    		currentVelocity = 0.0;
		    		init = false;
		    	} else {
		    		// update angular velocity based on change from past rotation angle and time
		    		double newvel = -(Math.toRadians(origin.rotsInfo.z)-z)/(System.currentTimeMillis()/1000.0 - initTime-time);
		    		currentVelocity = newvel;
		    		// if pretty much 0 just set to plain 0
		    		if (currentVelocity > -0.001 && currentVelocity < 0.001) {
		    			currentVelocity = 0.0;
		    		}
		    	}
		    	// update rotation
		    	z = Math.toRadians(origin.rotsInfo.z);
		    	
		    	// UPDATE THE CURRENT TIME OF OPERATION IN SECONDS
		    	time = (System.currentTimeMillis()/1000.0) - initTime;
		    	
		    	// CAN RESET TRACKED COINS AFTER A SPECIFIED TIMEFRAME
		    	// CURRENTLY RESETS WITH EACH COIN PICKUP
		    	/**
		    	// RESET SCENE AFTER SET TIME
		    	if (time > 10.0) {
		    		coinsPolar.clear();
		    		initTime = (System.currentTimeMillis()/1000.0);
		    	}
		    	con.addln("Time --> "+String.valueOf(time));
		    	*/
		    	
		    	// erode then dilate image prior to coin identification
		    	// reduces reflections and features to mostly show the coin color
		    	// has made the identification much more robust to lighting conditions
		    	cvErode(mainI, mainI, null, 3);
		    	cvDilate(mainI, mainI, null, 3);
		  	  	
		    	coinFinder.setImage(mainI);
		    	coinFinder.find();
	        	coinFinder.determineValues();
	        	
	        	// coin data obtained from coinFinder class
	        	ArrayList<TreeMap<Double, ArrayList<Double>>> coinData = coinFinder.getCoinLocationData();
	        	
	        	// contains:  ArrayList<TreeMap<Value, ArrayList<radius, angle, ID time>>>
	        	ArrayList<TreeMap<Double, ArrayList<Double>>> NEWcoinsPolar = new ArrayList<TreeMap<Double, ArrayList<Double>>>();
	        	
	        	// convert all identified coins into standard polar coordinates
	        	double offsetx = 320.0*pixelSize; double offsety = 240.0*pixelSize;
	        	for (TreeMap<Double, ArrayList<Double>> coin : coinData) {
	        		Double value = coin.firstKey();
	        		ArrayList<Double> pos = coin.get(value);
	        		double x = pos.get(0)-offsetx; double y = offsety-pos.get(1);
	        		double diffx = x-originXmm; double diffy = y-originYmm;
	        		Double polarRadius = Math.sqrt(Math.pow(diffx, 2)+Math.pow(diffy, 2));
	        		Double polarAngleRad = Math.atan2(diffy, diffx);
	        		TreeMap<Double, ArrayList<Double>> newmap = new TreeMap<Double, ArrayList<Double>>();
	        		ArrayList<Double> polarcoords = new ArrayList<Double>();
	        		polarcoords.add(polarRadius); polarcoords.add(polarAngleRad); polarcoords.add(time);
	        		newmap.put(value, polarcoords);
	        		NEWcoinsPolar.add(newmap);
	        	}
	        	
	        	// shift all tracked coins based on velocity
	        	for (TreeMap<Double, ArrayList<Double>> OLDcoin : coinsPolar) {
	        		Double OLDvalue = OLDcoin.firstKey();
        			ArrayList<Double> OLDpolar = OLDcoin.get(OLDvalue);
        			Double angle = OLDpolar.get(1);
        			Double NEWangle = angle-(OLDpolar.get(2)-time)*currentVelocity;
        			if (NEWangle > Math.PI) {
        				NEWangle -= 2.0*Math.PI;
        			} else if (NEWangle < -Math.PI) {
        				NEWangle += 2.0*Math.PI;
        			}
        			OLDpolar.set(1, NEWangle);
        			OLDpolar.set(2, time);
	        	}
	        	
	        	// check if newly identified coin is already being tracked, update information if required
	        	for (TreeMap<Double, ArrayList<Double>> NEWcoin : NEWcoinsPolar) {
	        		Double NEWvalue = NEWcoin.firstKey();
	        		ArrayList<Double> NEWpolar = NEWcoin.get(NEWvalue);
	        		
	        		if (NEWpolar.get(0) < 10.0) {
	        			continue;
	        		}
	        		
	        		// brute force compare
	        		boolean doAdd = true;
	        		for (TreeMap<Double, ArrayList<Double>> OLDcoin : coinsPolar) {
	        			Double OLDvalue = OLDcoin.firstKey();
	        			ArrayList<Double> OLDpolar = OLDcoin.get(OLDvalue);
	        			
	        			// if the radius and angle is within a threshold of propagated past identified coin...
	        			if ((OLDpolar.get(1).compareTo(NEWpolar.get(1)-0.25)>0) && (OLDpolar.get(1).compareTo(NEWpolar.get(1)+0.25)<0) &&
		        				(OLDpolar.get(0).compareTo(NEWpolar.get(0)-20)>0) && (OLDpolar.get(0).compareTo(NEWpolar.get(0)+20)<0)) {
	        				
	        				// if same coin but recognized as a larger value take larger value
	        				// this prevents misclassification of coins as silver when actually gold
	        				// (requirements for classification as silver is more broad than gold)
	        				if (OLDvalue > NEWvalue) {
	        					ArrayList<Double> newbit = new ArrayList<Double>();
	        					newbit.add(NEWpolar.get(0)); newbit.add(NEWpolar.get(1)); newbit.add(NEWpolar.get(2));
	        					TreeMap<Double, ArrayList<Double>> dat = new TreeMap<Double, ArrayList<Double>>();
	        					dat.put(OLDvalue, newbit);
	        					coinsPolar.set(coinsPolar.indexOf(OLDcoin), dat);
	        				}
	        				// otherwise add the new coin information in old coin place
	        				// (updates radius, angle and time)
	        				else {
	        					coinsPolar.remove(OLDcoin);
	        					coinsPolar.add(NEWcoin);
	        				}
	        				doAdd = false;
	        				break;
	        			}
	        		}
	        		// if not already in identified coins add as a new coin
	        		if (doAdd) {
	        			coinsPolar.add(NEWcoin);
	        		}
	        	}
	        	
	        	// shift all notes based on velocity
	        	for (TreeMap<String, ArrayList<Double>> OLDcoin : notesPolar) {
	        		String OLDvalue = OLDcoin.firstKey();
        			ArrayList<Double> OLDpolar = OLDcoin.get(OLDvalue);
        			Double angle = OLDpolar.get(1);
        			Double NEWangle = angle-(OLDpolar.get(2)-time)*currentVelocity;
        			OLDpolar.set(1, NEWangle);
        			OLDpolar.set(2, time);
	        	}
	        	
	        	IplImage trackedImage = coinFinder.getDrawnCoins();
	        	
	        	// CAN VISUALISE OPTICAL FLOW IF REQUIRED - UNCOMMENT THESE TWO LINES
	        	//OpticalFlowTracker flowTracker = new OpticalFlowTracker();
		    	//trackedImage = flowTracker.trackMovement(trackedImage, kr.getColorFrame());
		    	
	        	// DRAW CIRCLES
	        	Double TOTALVALUE = 0.0;
		    	boolean DRAWCIRCLES = true;
		    	if (DRAWCIRCLES) {
			    	for (TreeMap<String, ArrayList<Double>> note : notesPolar) {
		        		String label = note.firstKey();
		        		ArrayList<Double> polarcoord = note.get(label);
		        		double radius = polarcoord.get(0);
		        		double angle = polarcoord.get(1);
						CvPoint ORIGIN = new CvPoint((int)(originXmm/pixelSize)+320, (int)(-originYmm/pixelSize)+240);
						cvCircle(trackedImage, ORIGIN, (int)(radius/pixelSize), CvScalar.RED, 1, CV_AA, 0);
						
						double xWrtCent = (radius)*Math.cos(angle)/pixelSize;
						double yWrtCent = (radius)*Math.sin(angle)/pixelSize;
						double cartX = xWrtCent+ORIGIN.x(); double cartY = -yWrtCent+ORIGIN.y();
						CvPoint POINT = new CvPoint((int)cartX, (int)cartY);
						cvCircle(trackedImage, POINT, 5, CvScalar.GREEN, 2, CV_AA, 0);
						cvPutText(trackedImage, "  "+label, POINT, font, CvScalar.GREEN);
		        	}
			    	for (TreeMap<Double, ArrayList<Double>> note : coinsPolar) {
		        		Double value = note.firstKey();
		        		ArrayList<Double> polarcoord = note.get(value);
		        		double radius = polarcoord.get(0);
		        		double angle = polarcoord.get(1);
						CvPoint ORIGIN = new CvPoint((int)(originXmm/pixelSize)+320,(int)(-originYmm/pixelSize)+240);
						cvCircle(trackedImage, ORIGIN, (int)(radius/pixelSize), CvScalar.RED, 1, CV_AA, 0);
						
						double xWrtCent = (radius)*Math.cos(angle)/pixelSize;
						double yWrtCent = (radius)*Math.sin(angle)/pixelSize;
						double cartX = xWrtCent+ORIGIN.x(); double cartY = -yWrtCent+ORIGIN.y();
						CvPoint POINT = new CvPoint((int)cartX, (int)cartY);
						cvCircle(trackedImage, POINT, 10, CvScalar.GREEN, 1, CV_AA, 0);
						cvPutText(trackedImage, "  "+value.toString(), POINT, font, CvScalar.GREEN);
						
						// add up values
						TOTALVALUE += value;
		        	}
		    	}
		    	
		    	// update GUI picture on RHS
		    	w.ImagePanelUpdate(currentP, trackedImage, 1);
		    	
		    	// PRINT INFORMATION
		    	con.wipe();
	        	con.addln("Total value --> $"+TOTALVALUE.toString());
	        	//con.addln(notesPolar.toString());
	        	//con.addln(coinsPolar.toString());
	        	//con.addln("Velocity measured (rad/s) --> "+currentVelocity.toString());
	        	//con.addln("Velocity read (rad/s) --> "+turnTable.readSpeed().toString());

	        	if (currentVelocity != 0) {
		        	if (time > (movingCoinTime + 2.0 + 4.0/currentVelocity)) {
	        			coinsPolar.clear();
			    		initTime = (System.currentTimeMillis()/1000.0);
			    		movingCoinTime = 1000;
	        		}
	        	}
	        	
	        	// actuation time prediction requires a good estimate of rotational velocity
	        	// the last 40 velocities is taken and the average of the median 20 used
	        	velHistory.add(currentVelocity);
	        	while (velHistory.size() > 40) {
	        		velHistory.remove(0);
	        	}
	        	
	        	if (velHistory.size() == 40) {
	        		ArrayList<Double> ordered = (ArrayList<Double>) velHistory.clone();
	        		Arrays.sort(ordered.toArray());
		        	// average of the median
		        	double sum = 0;
		        	for (int i=10; i < 30; i++) {
		        		sum += ordered.get(i);
		        	}
		        	double avgVel = sum/20.0;
		//        	con.addln("Average Velocity measured (rad/s) --> "+avgVel);
		        	
		        	// ACTUATE ARM IF COIN IS REACHABLE
		        	for (TreeMap<Double, ArrayList<Double>> coin : coinsPolar) {
		        		Double value = coin.firstKey();
		        		ArrayList<Double> polarcoord = coin.get(value);
		        		Double radius = polarcoord.get(0);
		        		double angle = polarcoord.get(1);
		        		double deltaAng = 0.0;
		        		
		        		// if in the semicircle left of the arm (right side of screen) and moving towards arm
		        		if ((currentVelocity > 0) && (angle > -Math.PI/2.0) && (angle < Math.PI/2.0)) {
		        			// calculate the angle corresponding to required actuation time
		        			if (radius > 10.0 && radius < 20.0) {
		        				deltaAng = Math.PI/2.0 - avgVel*4.8;
		        			} else if (radius > 20 && radius < 25) {
		        				deltaAng = Math.PI/2.0 - avgVel*4.91;
		        			} else if(radius > 25 && radius < 30) {
		        				deltaAng = Math.PI/2.0 - avgVel*3.98;
		        			} else if (radius > 30 && radius < 40) {									// 
		        				deltaAng = Math.PI/2.0 - avgVel*1.46;
		        			} else if (radius > 40 && radius < 50) {									// GOOD
		        				deltaAng = Math.PI/2.0 - avgVel*1.35;
		        			} else if (radius > 50 && radius < 60) {									// GOOD
		        				deltaAng = Math.PI/2.0 - avgVel*0.95;
		        			} else if (radius > 60 && radius < 70) {									// GOOD
		        				deltaAng = Math.PI/2.0 - avgVel*0.85;
		        			} else if (radius > 70 && radius < 80) {									// GOOD
		        				deltaAng = Math.PI/2.0 - avgVel*0.8;
		        			} else if (radius > 80 && radius < 90) {									// GOOD
		        				deltaAng = Math.PI/2.0 - avgVel*1.04;
		        			} else if (radius > 90 && radius < 100) {									// GOOD
		        				deltaAng = Math.PI/2.0 - avgVel*1.38;
		        			}
		        			
		        			// if the coin is +- 4 degrees of this angle actuate!
		        			// (as long as not already actuating)
		        			Double window = Math.toRadians(4);
		        			if ((angle > deltaAng-window) && (angle < deltaAng+window)
		        					&& (!armThread.isAlive())) {
		        				// arm is a threaded process to allow the GUI to still update
		        				arm.setThread(radius.intValue(), arm.getBoxNum(value));
		        				armThread = new Thread(arm, "armThread");
		        				armThread.start();
		        				movingCoinTime = time;
		        				break;
		        			}
		        		}
		        		
		        		// if in the quadrant right of the arm (top left of screen) and moving towards arm
		        		else if ((currentVelocity < 0) && (angle > Math.PI/2.0) && (angle < Math.PI)) {
		        			// calculate the angle corresponding to required actuation time
		        			if (radius > 10.0 && radius < 20.0) {
		        				deltaAng = Math.PI/2.0 - avgVel*4.5;
		        			} else if (radius > 20 && radius < 25) {
		        				deltaAng = Math.PI/2.0 - avgVel*4.61;
		        			} else if(radius > 25 && radius < 30) {
		        				deltaAng = Math.PI/2.0 - avgVel*3.68;
		        			} else if (radius > 30 && radius < 40) {
		        				deltaAng = Math.PI/2.0 - avgVel*1.16;
		        			} else if (radius > 40 && radius < 50) {
		        				deltaAng = Math.PI/2.0 - avgVel*1.05;
		        			} else if (radius > 50 && radius < 60) {
		        				deltaAng = Math.PI/2.0 - avgVel*0.65;
		        			} else if (radius > 60 && radius < 70) {
		        				deltaAng = Math.PI/2.0 - avgVel*0.55;
		        			} else if (radius > 70 && radius < 80) {
		        				deltaAng = Math.PI/2.0 - avgVel*0.5;
		        			} else if (radius > 80 && radius < 90) {
		        				deltaAng = Math.PI/2.0 - avgVel*0.74;
		        			} else if (radius > 90 && radius < 100) {
		        				deltaAng = Math.PI/2.0 - avgVel*1.08;
		        			}
		        			
		        			// if the coin is +- 4 degrees of this angle actuate!
		        			// (as long as not already actuating)
		        			Double window = Math.toRadians(4);
		        			if ((angle < deltaAng+window) && (angle > deltaAng-window)
		        					&& (!armThread.isAlive())) {
		        				// arm is a threaded process to allow the GUI to still update
		        				con.addln("INSIDE WINDOW");
		        				arm.setThread(radius.intValue(), arm.getBoxNum(value));
		        				armThread = new Thread(arm, "armThread");
		        				armThread.start();
		        				movingCoinTime = time;
		        				break;
		        			}
		        		}
	        		}
	        	}
			}
		}
	}
}
