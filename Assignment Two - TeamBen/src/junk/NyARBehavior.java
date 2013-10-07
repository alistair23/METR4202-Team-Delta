package junk;


// NyARBehavior.java
// Andrew Davison, ad@fivedots.coe.psu.ac.th, July 2013

/* A time-based triggered behaviour which uses a detector to
   update the position of a colored cube drawn above markers.
*/

import java.io.*;
import java.awt.image.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import jp.nyatla.nyartoolkit.utils.j2se.*;
import jp.nyatla.nyartoolkit.core.*;
import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.detector.*;
import jp.nyatla.nyartoolkit.core.types.*;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;


import capture.KinectReader;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.videoInputLib.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.avutil.*;   // for grabber/recorder constants



public class NyARBehavior extends Behavior
{
  /* dimensions of each image; the panel is the same size as the image */
  private static final int WIDTH = 640;  
  private static final int HEIGHT = 480;

  private static final int CAMERA_ID = 0;

  private final String CARCODE_FILE = "patt.hiro";
  private final double FPS = 30.0;

 private final double MARKER_SIZE = 0.095; 
                 // 95 cm width and height in Java 3D world units

  private NyARBufferedImageRaster raster;
  private ImageComponent2D imc2d;

  private NyARSingleDetectMarker detector;

  private NyARDoubleMatrix44 transMat = new NyARDoubleMatrix44();
  private TransformGroup tg;
  private Background bg;
  private WakeupCondition wakeup;
  private boolean cameraStopped = false;

  KinectReader kr = new KinectReader();

  public NyARBehavior(NyARParam cameraParams, TransformGroup tg, Background bg)
  {
	  
    super();
    
    this.tg = tg;
    this.bg = bg;

    wakeup = new WakeupOnElapsedTime((int)(1000.0/FPS));
    setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));


    
    BufferedImage im = kr.getColorFrame().getBufferedImage();

    try {
      raster = new NyARBufferedImageRaster(im);
      imc2d = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, im, true, false);
      imc2d.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);
      
      NyARCode markerInfo = NyARCode.createFromARPattFile(
                                          new FileInputStream(CARCODE_FILE),16, 16);
      detector = NyARSingleDetectMarker.createInstance(
                                          cameraParams, markerInfo, MARKER_SIZE);
    }
    catch(Exception e)
    {  System.out.println(e);
       System.exit(1);
    }
  }  // end of NyARSingleMarkerBehaviorHolder()




  

  private IplImage picGrab(FrameGrabber grabber, int ID)
  {
    IplImage im = null;
    try {
      im = kr.getColorFrame();  // take a snap
    }
    catch(Exception e) 
    {  System.out.println("Problem grabbing image for camera " + ID);  }
    return im;
  }  // end of picGrab()



  public void stop()
  {
    try {
      cameraStopped = true;
      setEnable(false);
      kr.Stop();
    }
    catch(Exception e) 
    {  System.out.println("Problem stopping grabbing for camera " + CAMERA_ID);  }
  }


  public void initialize()
  {   wakeupOn(wakeup);  }



  public void processStimulus(Enumeration criteria)
  /* use the detector to update the colored cube's position on the markers */
  {
    if (cameraStopped)
      return;
    try {
      BufferedImage im = kr.getColorFrame().getBufferedImage();
      raster.wrapImage(im);

      if (raster.hasBuffer()) {
        if (bg != null) {
          imc2d.set( raster.getBufferedImage());
          bg.setImage(imc2d);
        }
        boolean foundMarker = detector.detectMarkerLite(raster, 120);
        if (foundMarker) {
          detector.getTransmat(transMat);
          Matrix4d matrix = new Matrix4d(
              -transMat.m00, -transMat.m10, transMat.m20, 0,
              -transMat.m01, -transMat.m11, transMat.m21, 0,
              -transMat.m02, -transMat.m12, transMat.m22, 0,
              -transMat.m03, -transMat.m13, transMat.m23, 1);
          matrix.transpose();
          Transform3D t3d = new Transform3D(matrix);
          if (tg != null)
            tg.setTransform(t3d);
        }else{
        	System.out.println("No marker found");
        }
      }
      wakeupOn(wakeup);
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }  // end of processStimulus()



}  // end of NyARBehavior class

