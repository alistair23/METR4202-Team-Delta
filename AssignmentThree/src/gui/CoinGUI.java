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
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Media;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import localisation.AxisLocator;
import colorCalibration.BlackBalance;
import colorCalibration.BlobFinder;
import colorCalibration.ColorChart;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import communication.TurnTableSerial;

import cameraCalibration.CameraCalibrator;
import capture.*;
import functions.*;

/**
 * @author Ben Merange
 *
 */

public class CoinGUI extends JFrame{

	static boolean SIFTING = true;
	
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
 	
 	static Double originXmm = null;
 	static Double originYmm = null;
 	
 	static ArrayList<TreeMap<String, ArrayList<Double>>> notesPolar = new ArrayList<TreeMap<String, ArrayList<Double>>>();
 	static CvFont font = new CvFont(CV_FONT_HERSHEY_PLAIN, 1, 1);
 	static double pixelSize = 0.0;
 	static CoinFinder coinFinder;
 	
	static ArrayList<TreeMap<Double, ArrayList<Double>>> coinsPolar = new ArrayList<TreeMap<Double, ArrayList<Double>>>();
	
	static double initTime = 0;
	static Double currentVelocity = 0.0;
 	
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
	    
	    //double height = 375.0;
	    double height = 375.0;
	    if (haveKinect) {
		    mainI = kr.getColorFrame();
		    coinFinder = new CoinFinder(mainI, height);
		    pixelSize = coinFinder.getPixelSize();
		    while (originXmm == null || originYmm == null) {
		    	mainI = kr.getColorFrame();
			    AxisLocator origin = new AxisLocator(mainI);
			    CvMat transMatrix = origin.findAxis();
			    if (transMatrix != null) {
			    	originXmm = -1000*transMatrix.get(0, 3);
				    originYmm = 1000*transMatrix.get(1, 3);
			    }
		    }
		    
		    // initial sifting and centering
		    if (SIFTING) {
		    	con.addln("Finding notes...");
		    	int SIFTTHRESHOLD = 210;
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
					for (int i=0; i < 5; i++) {
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
					if (xArray.length > 10 && yArray.length > 10) {
						int x = xArray[(int) (((double)xArray.length)/2.0)];
						int y = yArray[(int) (((double)yArray.length)/2.0)];
					    CvPoint POINT = cvPointFrom32f(new CvPoint2D32f(x, y));
					    labels.add(name); locations.add(POINT);
					    Integer len = xArray.length;
					    con.addln(len.toString());
			//		    IplImage debugImage = sifter.drawMatchPoints(kr.getColorFrame().clone());
			//		    cvShowImage("debug", debugImage);
			//		    cvWaitKey(0);
					}
				}
				
				IplImage pointsDrawn = kr.getColorFrame().clone();
				// need coin finder for pixel size
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
	    //update the main window with the camera feed forever
	    initTime = System.currentTimeMillis()/1000.0;
	//    TurnTableSerial turnTable = new TurnTableSerial();
	    
	    int takeVel = 0;
	    ArrayList<Double> lastVels = new ArrayList<Double>();
	    
	    AxisLocator origin = new AxisLocator(mainI);
	    double time = (System.currentTimeMillis()/1000.0) - initTime;
	    double z = 0.0;
	    boolean init = true;
	    
		while(true){
			if(haveKinect){
				mainI = kr.getColorFrame();
		    	w.ImagePanelUpdate(mainP, mainI, 1);
		    	
		    	origin.setImage(mainI);
		    	origin.findAxis();
		    	
		    	if (init == true) {
		    		currentVelocity = 0.0;
		    		init = false;
		    	} else {
		    		currentVelocity = -(Math.toRadians(origin.rotsInfo.z)-z)/(System.currentTimeMillis()/1000.0 - initTime-time);
		    	}
		    	
		    	time = (System.currentTimeMillis()/1000.0) - initTime;
		    	
		    	// RESET SCENE AFTER SET TIME
		    	if (time > 10.0) {
		    		coinsPolar.clear();
		    		initTime = (System.currentTimeMillis()/1000.0);
		    	}
		    	con.addln("Time --> "+String.valueOf(time));
		    	
		    	z = Math.toRadians(origin.rotsInfo.z);
		    	
		    	//currentVelocity = turnTable.readSpeed();
		    	
		    	//w.ImagePanelUpdate(currentP, findCoins(mainI), 1);
		    	
		    	cvErode(mainI, mainI, null, 3);
		    	cvDilate(mainI, mainI, null, 3);
		  	  	
		    	coinFinder.setImage(mainI);
		    	coinFinder.find();
	        	coinFinder.determineValues();
	        	
	        	ArrayList<TreeMap<Double, ArrayList<Double>>> coinData = coinFinder.getCoinLocationData();
	        	
	        	// radius, angle, time
	        	ArrayList<TreeMap<Double, ArrayList<Double>>> NEWcoinsPolar = new ArrayList<TreeMap<Double, ArrayList<Double>>>();
	        	
	        	// wrt centre in standard orientation
	        	double offsetx = 320.0*pixelSize; double offsety = 240.0*pixelSize;
	        	for (TreeMap<Double, ArrayList<Double>> coin : coinData) {
	        		Double value = coin.firstKey();
	        		ArrayList<Double> pos = coin.get(value);
	        		double x = pos.get(0)-offsetx; double y = offsety-pos.get(1);
	        		double diffx = x-originXmm; double diffy = y-originYmm;
	        		//System.out.println(value+" --> diffx: "+diffx+", diffy: "+diffy);
	        		Double polarRadius = Math.sqrt(Math.pow(diffx, 2)+Math.pow(diffy, 2));
	        		Double polarAngleRad = Math.atan2(diffy, diffx);
	        		//Double polarAngleDeg = Math.toDegrees(polarAngleRad);
	        		//con.wipe(); 
	        		//con.addln("Radius: "+polarRadius.toString());
	        		//con.addln("Angle: "+polarAngleRad.toString());
	        		
	        		TreeMap<Double, ArrayList<Double>> newmap = new TreeMap<Double, ArrayList<Double>>();
	        		ArrayList<Double> polarcoords = new ArrayList<Double>();
	        		polarcoords.add(polarRadius); polarcoords.add(polarAngleRad); polarcoords.add(time);
	        		newmap.put(value, polarcoords);
	        		NEWcoinsPolar.add(newmap);
	        	}
	        	
	        	// shift all old based on velocity
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
	        	
	        	TreeMap<Double, Double> hardMapSize = new TreeMap<Double, Double>();
	        	hardMapSize.put(0.05, 19.4); hardMapSize.put(0.1, 23.6);
	        	hardMapSize.put(0.2, 28.5); hardMapSize.put(0.5, 31.5);
	        	hardMapSize.put(1.0, 25.0); hardMapSize.put(2.0, 20.6);
	        	
	        	// check new vs old
	        	for (TreeMap<Double, ArrayList<Double>> NEWcoin : NEWcoinsPolar) {
	        		Double NEWvalue = NEWcoin.firstKey();
	        		ArrayList<Double> NEWpolar = NEWcoin.get(NEWvalue);
	        		// brute force compare
	        		boolean doAdd = true;
	        		for (TreeMap<Double, ArrayList<Double>> OLDcoin : coinsPolar) {
	        			Double OLDvalue = OLDcoin.firstKey();
	        			ArrayList<Double> OLDpolar = OLDcoin.get(OLDvalue);
	        			/**
	        			// compare angles
	        			double radiusThreshold = 0.0;
	        			double angleThreshold = 0.0;
	        			if (OLDvalue.equals(NEWvalue)) {
	        				radiusThreshold = hardMapSize.get(NEWvalue)/2.0 + 5.0;
	        				angleThreshold = Math.PI - 2.0*Math.acos(hardMapSize.get(NEWvalue)/(2.0*NEWpolar.get(0))) + 0.1;
	        			} else {
	        				double OLDrad = hardMapSize.get(OLDvalue)/2.0;
		        			double NEWrad = hardMapSize.get(NEWvalue)/2.0;
		        			if (OLDrad > NEWrad) {
		        				radiusThreshold = OLDrad + 5.0;
		        				angleThreshold = Math.PI - 2.0*Math.acos(OLDrad/(2.0*OLDpolar.get(0))) + 0.1;
		        			} else {
		        				radiusThreshold = NEWrad+5.0;
		        				angleThreshold = Math.PI - 2.0*Math.acos(NEWrad/(2.0*NEWpolar.get(0))) + 0.1;
		        			}
	        			}
	        			*/
	        			//if ((OLDpolar.get(1) > (NEWpolar.get(1)-angleThreshold)) && (OLDpolar.get(1) < (NEWpolar.get(1)+angleThreshold)) &&
	        			//		(OLDpolar.get(0) > (NEWpolar.get(0)-radiusThreshold)) && (OLDpolar.get(0) < (NEWpolar.get(0)+radiusThreshold))) {
	        			
	        			if ((OLDpolar.get(1).compareTo(NEWpolar.get(1)-0.25)>0) && (OLDpolar.get(1).compareTo(NEWpolar.get(1)+0.25)<0) &&
		        				(OLDpolar.get(0).compareTo(NEWpolar.get(0)-20)>0) && (OLDpolar.get(0).compareTo(NEWpolar.get(0)+20)<0)) {
	        				
	        				if (OLDvalue > NEWvalue) {
	        					//NEWcoin.put(OLDvalue, NEWpolar);
	        					//OLDcoin = NEWcoin;
	        					
	        					ArrayList<Double> newbit = new ArrayList<Double>();
	        					newbit.add(NEWpolar.get(0)); newbit.add(NEWpolar.get(1)); newbit.add(NEWpolar.get(2));
	        					TreeMap<Double, ArrayList<Double>> dat = new TreeMap<Double, ArrayList<Double>>();
	        					dat.put(OLDvalue, newbit);
	        					
	        					//System.out.println("coinsPolar "+coinsPolar);
	        					//System.out.println("OLDvalue "+OLDvalue);
	        					coinsPolar.set(coinsPolar.indexOf(OLDcoin), dat);
	        					
	    //    					if (NEWcoin.firstKey() == 0.5) {
	    //	        				con.addln("IN THRESH "+NEWcoin.toString());
	    //	        			}
	        					//coinsPolar.remove(OLDcoin);
	        					//coinsPolar.add(dat);
	        					
	        					//OLDcoin.put(OLDvalue,NEWpolar);
	        					//coinsPolar.remove(OLDcoin);
	        					//coinsPolar.add(NEWcoin);
	        					
	        				} else {
	        					//OLDcoin = NEWcoin;
	        					coinsPolar.remove(OLDcoin);
	        					coinsPolar.add(NEWcoin);
	     //   					if (NEWcoin.firstKey() == 0.5) {
	    //	        				con.addln("OUTSIDE THRESH "+NEWcoin.toString());
	    //	        			}
	        				}
	        				
	        				doAdd = false;
	        				break;
	        			}
	        		}
	        		if (doAdd) {
	        			coinsPolar.add(NEWcoin);
	    //    			if (NEWcoin.firstKey() == 0.5) {
	     //   				con.addln("ADD NEW "+NEWcoin.toString());
	     //   			}
	        			//System.out.println(NEWcoin);
	        		}
	        	}
	        	
	        	// shift all notes based on velocity
	        	for (TreeMap<String, ArrayList<Double>> OLDcoin : notesPolar) {
	        		String OLDvalue = OLDcoin.firstKey();
        			ArrayList<Double> OLDpolar = OLDcoin.get(OLDvalue);
        			Double angle = OLDpolar.get(1);
        			Double NEWangle = angle-(OLDpolar.get(2)-time)*currentVelocity;
        			//Double NEWangle = angle;
        			OLDpolar.set(1, NEWangle);
        			OLDpolar.set(2, time);
	        	}
	        	
	        	IplImage trackedImage = coinFinder.getDrawnCoins();
	        	
	        	//OpticalFlowTracker flowTracker = new OpticalFlowTracker();
		    	//trackedImage = flowTracker.trackMovement(trackedImage, kr.getColorFrame());
		    	
		    	//con.wipe();
				//con.addln(coinFinder.getValues().toString());
				
	        	Double TOTALVALUE = 0.0;
		    	// DRAW CIRCLES
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
		    	
		    	w.ImagePanelUpdate(currentP, trackedImage, 1);
		    	
		    	con.wipe();
		    	// ONLY VALUE IN CURRENT COIN FINDER CLASS
	        	//con.addln("$"+coinFinder.getTotalValue().toString());
	        	con.addln("Total value --> $"+TOTALVALUE.toString());
	        	//con.addln(notesPolar.toString());
	        	//con.addln(coinsPolar.toString());
	        	con.addln("Velocity measured (rad/s) --> "+currentVelocity.toString());
	        	//con.addln("Velocity read (rad/s) --> "+turnTable.readSpeed().toString());
	        	
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
