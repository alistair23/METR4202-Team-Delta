package localisation;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;

import javax.media.j3d.Background;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4d;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.detector.NyARSingleDetectMarker;
import jp.nyatla.nyartoolkit.java3d.utils.J3dNyARParam;
import jp.nyatla.nyartoolkit.utils.j2se.NyARBufferedImageRaster;
import jp.nyatla.nyartoolkit.core.*;
import jp.nyatla.nyartoolkit.java3d.utils.*;


public class AxisLocator {

	private static NyARBufferedImageRaster raster;
		IplImage image;
	  private NyARSingleDetectMarker detector;

	  private NyARDoubleMatrix44 transMat = new NyARDoubleMatrix44();
	  private TransformGroup tg;
	  private Background bg;
	  private ImageComponent2D imc2d;
	  private final String CARCODE_FILE = "patt.kanji";

	  private final double MARKER_SIZE = 0.035; // 35 mm width and height in Java 3D world units
	  private J3dNyARParam cameraParams;
	  private final String PARAMS_FNM = "camera_para.dat";
	  
	
	public AxisLocator(IplImage image){
		
		 Loader.load(opencv_objdetect.class);
		
		this.setImage(image);
		
		 
	    try {
	      cameraParams = J3dNyARParam.loadARParamFile( new FileInputStream(PARAMS_FNM));
	      //cameraParams.changeScreenSize(WIDTH, HEIGHT);
	    }
	    catch(Exception e)
	    {  System.out.println("Could not read camera parameters from " + PARAMS_FNM);
	       System.exit(1);
	    }

		
		 
			      try {
					raster = new NyARBufferedImageRaster(image.getBufferedImage());

			      imc2d = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, image.getBufferedImage(), true, false);
			      imc2d.setCapability(ImageComponent.ALLOW_IMAGE_WRITE);
			      
			      NyARCode markerInfo = NyARCode.createFromARPattFile(new FileInputStream(CARCODE_FILE),16, 16);
			      detector = NyARSingleDetectMarker.createInstance(cameraParams, markerInfo, MARKER_SIZE);
					} catch (NyARException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		
	}
	
	public static void main(String[] args) {
		IplImage image = cvLoadImage("test_images/ax.png");
		AxisLocator al = new AxisLocator(image);
		al.findAxis(image);
	}
	
	public void setImage(IplImage image){
		this.image = image;
	}
		
		
		 public void findAxis(IplImage img){
		  /* use the detector to update the colored cube's position on the markers */
		      try {
				raster.wrapImage(image.getBufferedImage());
				if (raster.hasBuffer()) {
					if (bg != null) {
						imc2d.set( raster.getBufferedImage());
						bg.setImage(imc2d);
					}
					boolean foundMarker = detector.detectMarkerLite(raster, 120);
					
					if (foundMarker) {
						detector.getTransmat(transMat);
						System.out.println("Matrixing.");
						Matrix4d matrix = new Matrix4d(
								-transMat.m00, -transMat.m10, transMat.m20, 0,
								-transMat.m01, -transMat.m11, transMat.m21, 0,
								-transMat.m02, -transMat.m12, transMat.m22, 0,
								-transMat.m03, -transMat.m13, transMat.m23, 1);
						System.out.println(matrix.toString());
						matrix.transpose();
						Transform3D t3d = new Transform3D(matrix);
						if (tg != null){
							tg.setTransform(t3d);
						}
					}else{
						System.out.println("No Marker Found!");
					}
				} 
			} catch (NyARException e) {
				e.printStackTrace();
			}
      
		 }  // end of processStimulus()
	

}
