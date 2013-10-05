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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import cameraCalibration.CameraCalibrator;
import capture.ImageConverter;
import capture.KinectReader;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class mainWindow extends JFrame {

    GridBagConstraints gc = new GridBagConstraints();
    GridBagLayout g = new GridBagLayout();
    Dimension d = new Dimension(1200, 1000); //size of window
    
    int buttons = 4; // number of buttons at top.
    
    JButton Capture = new JButton("Capture");
    JButton Save = new JButton("Save");
    JButton Load = new JButton("Load");
    JButton Exit = new JButton("Exit");
    
    JPanel mainPanelCalibrator = new JPanel(new GridBagLayout());
    JPanel mainPanelColor = new JPanel(new GridBagLayout());
    JPanel mainPanelAnalysis = new JPanel(new GridBagLayout());
    JPanel mainPanelOptions = new JPanel(new GridBagLayout());
    
    JPanel source = new JPanel();
    JPanel mod = new JPanel();
    
    static IplImage mainimg = cvLoadImage("test_images/ti.png");
    static IplImage currentimg = cvLoadImage("test_images/ti.png");
    
    
    
    ConsolePanel con = new ConsolePanel();
    KinectReader kr = new KinectReader(); 
    CameraCalibrator cc = new CameraCalibrator();
    
    public mainWindow(){
    	
    	//TODO kr.Start();
    	cc.setup();
    	
    	//set layout
    	this.setLayout(g);
        this.setVisible(true);
        this.setSize(d);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        
        //add buttons to window
        Capture.setMinimumSize(new Dimension(300, 50));
        Save.setMinimumSize(new Dimension(300, 50));
        Load.setMinimumSize(new Dimension(300, 50));
        Exit.setMinimumSize(new Dimension(300, 50));

        Capture.setPreferredSize(new Dimension(300, 50));
        Save.setPreferredSize(new Dimension(300, 50));
        Load.setPreferredSize(new Dimension(300, 50));
        Exit.setPreferredSize(new Dimension(300, 50));

        this.add(Capture,0,0,1,1,0);
        this.add(Save,1,0,1,1,0);
        this.add(Load,2,0,1,1,0);
        this.add(Exit,3,0,1,1,0);
        
        
        //add button listeners
        Capture.addActionListener(new ActionListener() {
    		 
            public void actionPerformed(ActionEvent e)
            {
            	//TODO setImage(kr.getColorFrame()); 
            	
            	//tabbedPane.getSelectedIndex() // returns the currently selected tab
            }
        });  
        
        Save.addActionListener(new ActionListener() {
    		 
            public void actionPerformed(ActionEvent e)
            {
            	ImageConverter ic = new ImageConverter();

            	IplImage img = mainimg;
 
            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
            	
            	fc.showSaveDialog(new JFrame());
            	
            	String path = fc.getSelectedFile().getAbsolutePath();

            	con.addln("Saving Image to: "+path);
            	
            	ic.savePNG(path.substring(0, path.length()-4), mainimg);
            }
        });     
        
        Load.addActionListener(new ActionListener() {
    		 
            public void actionPerformed(ActionEvent e)
            {
            	final JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/test_images/");
            	
            	fc.showOpenDialog(new JFrame());
            	
            	String path = fc.getSelectedFile().getAbsolutePath();
            	
            	con.addln("Loading Image From: "+path);
            	
            	mainimg = cvLoadImage(path);
            	
            	setImage();
            }
        });   
        
        Exit.addActionListener(new ActionListener() {
    		 
            public void actionPerformed(ActionEvent e)
            {
            	exit();
            }
        });      
        
        //Setup the tab panel
        JTabbedPane tabbedPane = new JTabbedPane();
        
        add(tabbedPane, 0, 1, buttons, 1,1);

        tabbedPane.addTab("Camera Calibrator", null, mainPanelCalibrator, "Calibrate the Camera");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        tabbedPane.addTab("Color Calibrator", null, mainPanelColor, "Calibrate the colors of the camera");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        tabbedPane.addTab("Analysis", null, mainPanelAnalysis, "Capture a scene and Analyse it for Coins");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        tabbedPane.addTab("Options", null, mainPanelOptions, "Modify Program Options");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        
        //Populate the Camera calibration panel
        add(mainimg, source, 1);
        add(currentimg, mod, 1);
        
        add(source, mainPanelCalibrator, 0, 1, 4, 1);
        add(mod, mainPanelCalibrator, 4, 1, 4, 1);
        
        final JPanel samples = new JPanel();
        add(samples, mainPanelCalibrator, 0, 2, 8, 1);

        
        JButton FindCB = new JButton("Find Checkerboard");
        FindCB.addActionListener(new ActionListener() {
   		 
            public void actionPerformed(ActionEvent e)
            {
            	con.add("Looking for Chess Board... ");
            	currentimg = cc.FindChessboard(mainimg);
            	if(cc.patternFound > 0){
            		con.add(cc.patternFound+" Pattern(s) Found!");
            		con.newln();
            	setImage();
            	}else{
            		con.add("No Chessboard Found!");
            		con.newln();
            	}
            }
        });      
 
        JButton add2calib = new JButton("Add To Calibrator");
        add2calib.addActionListener(new ActionListener() {
   		 
            public void actionPerformed(ActionEvent e)
            {
            	if(cc.Samples == cc.SampleAt){
            		con.addln("Sample Not Added. Sample Limit Reached!");
            	}else{
            	boolean has = cc.addToCalibration(mainimg);
            	if(has == true){
            	con.addln("Calibration Image added at "+cc.SampleAt+"/"+cc.Samples);
            	samples.add(new JLabel(new ImageIcon(resize(mainimg.clone().getBufferedImage(),mainimg.width()/4,mainimg.height()/4))),gc);
            	revalidate();
            	}else{
            		con.addln("Sample Not Added. No Board Found!");
            	}
            	
            	}
            	
            	
            }
        });      
 
        JButton runcalib = new JButton("Run Calibration");
        runcalib.addActionListener(new ActionListener() {
   		 
            public void actionPerformed(ActionEvent e)
            {
            	con.add("Performing Calibration on "+cc.Samples+" Samples...");
            	con.addln("Error = "+cc.calibrate());
            }
        });      
 
        JButton AutoCalib = new JButton("Remap Image");
        AutoCalib.addActionListener(new ActionListener() {
   		 
            public void actionPerformed(ActionEvent e)
            {
            	currentimg = cc.remap(mainimg);
            	setImage();
            	
            }
        });      
 
        add(FindCB, mainPanelCalibrator, 0, 0, 2, 1);
        add(add2calib, mainPanelCalibrator, 2, 0, 2, 1);
        add(runcalib, mainPanelCalibrator, 4, 0, 2, 1);
        add(AutoCalib, mainPanelCalibrator, 6, 0, 2, 1);
        
        //TODO Populate the Color calibration panel
        mainPanelColor.add(new JLabel(new ImageIcon(resize(mainimg.getBufferedImage(),mainimg.width()/1,mainimg.height()/1))),gc);
        
        //TODO Populate the analysis panel.
        mainPanelAnalysis.add(new JLabel(new ImageIcon(resize(mainimg.getBufferedImage(),mainimg.width()/1,mainimg.height()/1))),gc);
        
        //TODO Populate the options panel
        mainPanelOptions.add(new JLabel(new ImageIcon(resize(mainimg.getBufferedImage(),mainimg.width()/1,mainimg.height()/1))),gc);
        
        //TODO add workflow to panel
        
        
        
        //add console to panel
        add(con, 0, 3, buttons, 1);
        
        
        this.revalidate();
        
    }
    
    public static void main(String[] args) {
    	
     	
  	
    	
    	 final mainWindow mw = new mainWindow();
    	//JPanel p = new JPanel();
    	//p.add(new JLabel("PANELLLLL"));
    	

    }
    
    

	public void add(Component p, int x, int y, int w, int h){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = w;
        gc.gridheight = h;
        
          this.add(p, gc);
        
		this.revalidate();
		
	}

	public void add(Component p,JPanel j, int x, int y, int w, int h){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.gridwidth = w;
        gc.gridheight = h;
        
          j.add(p, gc);
        
		this.revalidate();
		
	}

	public void add(IplImage p,JPanel j, int x, int y, int w, int h, int scale){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = w;
        gc.gridheight = h;
        
        j.removeAll();
        j.add(new JLabel(new ImageIcon(resize(p.getBufferedImage(),p.width()/scale,p.height()/scale))),gc);;
        
		this.revalidate();
		
	}

	public void add(Component p, int x, int y, int w, int h, int weighty){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = weighty;
        gc.gridwidth = w;
        gc.gridheight = h;
        
          this.add(p, gc);
        
		this.revalidate();
		
	}
	
	public void add(IplImage p, JPanel jp,int scale){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTH;
        gc.weightx = 1;
        gc.weighty = 1;
        
		
		
		jp.removeAll();
        jp.add(new JLabel(new ImageIcon(resize(p.getBufferedImage(),p.width()/scale,p.height()/scale))),gc);
        
		this.revalidate();
		
	}
	
	public void addM(IplImage p, JPanel jp,int scale){

		gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTH;
        gc.weightx = 1;
        gc.weighty = 1;
        
		
		
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
	
	public void setImage(IplImage img){
		mainimg = img;

		source.removeAll();
		mod.removeAll();
		
		source.add(new JLabel(new ImageIcon(resize(img.getBufferedImage(),img.width()/1,img.height()/1))),gc);
		mod.add(new JLabel(new ImageIcon(resize(img.getBufferedImage(),img.width()/1,img.height()/1))),gc);
		
		this.revalidate();
	}
	public void setImage(){
	
		source.removeAll();
		mod.removeAll();
		
		source.add(new JLabel(new ImageIcon(resize(mainimg.getBufferedImage(),mainimg.width()/1,mainimg.height()/1))),gc);
		mod.add(new JLabel(new ImageIcon(resize(currentimg.getBufferedImage(),currentimg.width()/1,currentimg.height()/1))),gc);
		
		mainPanelColor.removeAll();
        mainPanelColor.add(new JLabel(new ImageIcon(resize(currentimg.getBufferedImage(),currentimg.width()/1,currentimg.height()/1))),gc);

		
		this.revalidate();
	}
	

}