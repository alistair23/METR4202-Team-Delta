package localisation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.detector.NyARSingleDetectMarker;
import jp.nyatla.nyartoolkit.java3d.utils.J3dNyARParam;
import jp.nyatla.nyartoolkit.utils.j2se.NyARBufferedImageRaster;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class will fins a kanji pattern (patt.kanji) within an IplImage,
 * returning a CvMat of the transformation matrix w.r.t. the camera.
 * 
 */

public class AxisLocator {

	  private static NyARBufferedImageRaster raster;
	  IplImage image;
	  private NyARSingleDetectMarker detector;
	  private NyARDoubleMatrix44 transMat = new NyARDoubleMatrix44();
	  private final String CARCODE_FILE = "patt.kanji";
	  private final double MARKER_SIZE = 0.016; // 32 mm width and height in Java 3D world units
	  private J3dNyARParam cameraParams;
	  private final String PARAMS_FNM = "camera_para.dat";
	  
	  public Point3d rotsInfo = null;
	  
	public AxisLocator(IplImage image){
		
		Loader.load(opencv_objdetect.class);
		this.setImage(image);

	    try {
	      cameraParams = J3dNyARParam.loadARParamFile( new FileInputStream(PARAMS_FNM));
	    }
	    catch(Exception e)
	    {  System.out.println("Could not read camera parameters from " + PARAMS_FNM);
	       System.exit(1);
	    }
	    
		try {
			raster = new NyARBufferedImageRaster(image.getBufferedImage());
			      
			NyARCode markerInfo = NyARCode.createFromARPattFile(new FileInputStream(CARCODE_FILE),16, 16);
			detector = NyARSingleDetectMarker.createInstance(cameraParams, markerInfo, MARKER_SIZE);
		} catch (NyARException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setImage(IplImage image){
		this.image = image;
	}
	
	private double roundToNumPlaces(double val, int numPlaces) 
	  {
	    double power = Math.pow(10, numPlaces);
	    long temp = Math.round(val*power);
	    return ((double)temp)/power;
	  }
	
	 private void calcEulerRots()
	  /* calculate the Euler rotation angles from the upper 3x3 rotation
	     components of the 4x4 transformation matrix. 
	     Based on code by Daniel Selman, December 1999
	     which is based on pseudo-code in "Matrix and Quaternion FAQ", Q37
	       http://www.j3d.org/matrix_faq/matrfaq_latest.html
	  */
	  {
	    rotsInfo = new Point3d();

	    rotsInfo.y = -Math.asin(transMat.m20);
	    double c = Math.cos(rotsInfo.y);

	    double tRx, tRy, tRz;
	    if(Math.abs(rotsInfo.y) > 0.00001) {
	      tRx = transMat.m22/c;
	      tRy = -transMat.m21/c;
	      rotsInfo.x = Math.atan2(tRy, tRx);

	      tRx = transMat.m00/c;
	      tRy = -transMat.m10/c;
	      rotsInfo.z = Math.atan2(tRy, tRx);
	    }
	    else {
	      rotsInfo.x  = 0.0;

	      tRx = transMat.m11;
	      tRy = transMat.m01;
	      rotsInfo.z = Math.atan2(tRy, tRx);
	    }

	    rotsInfo.x = -rotsInfo.x;
	    rotsInfo.z = -rotsInfo.z;

	    // ensure the values are positive by adding 2*PI if necessary...
	    if(rotsInfo.x < 0.0)
	    rotsInfo.x += 2*Math.PI;

	    if(rotsInfo.y < 0.0)
	      rotsInfo.y += 2*Math.PI;

	    if(rotsInfo.z < 0.0)
	      rotsInfo.z += 2*Math.PI;

	    // convert to degrees and round
	    rotsInfo.x = roundToNumPlaces( Math.toDegrees(rotsInfo.x), 0);
	    rotsInfo.y = roundToNumPlaces( Math.toDegrees(rotsInfo.y), 0);
	    rotsInfo.z = roundToNumPlaces( Math.toDegrees(rotsInfo.z), 0);

	    // System.out.println(getNameInfo() + " rots (" + 
	    //         rotsInfo.x + ", " + rotsInfo.y + ", " + rotsInfo.z + ")");
	  }  // end of calcEulerRots()

	public CvMat findAxis(){
		/* use the detector to update the colored cube's position on the markers */
		try {
			raster.wrapImage(image.getBufferedImage());
			if (raster.hasBuffer()) {

				boolean foundMarker = detector.detectMarkerLite(raster, 120)
							&& ((Double)detector.getConfidence()).compareTo(0.3) > 0;
					
				if (foundMarker) {
					detector.getTransmat(transMat);
					
		//			System.out.println("Matrixing.");
					CvMat matrix = CvMat.create(4, 4);
						
					matrix.put(0, 0, -transMat.m00);
					matrix.put(1, 0, -transMat.m10);
					matrix.put(2, 0, transMat.m20);
					matrix.put(3, 0, 0);

					matrix.put(0, 1, -transMat.m01);
					matrix.put(1, 1, -transMat.m11);
					matrix.put(2, 1, transMat.m21);
					matrix.put(3, 1, 0);

					matrix.put(0, 2, -transMat.m02);
					matrix.put(1, 2, -transMat.m12);
					matrix.put(2, 2, transMat.m22);
					matrix.put(2, 2, 0);

					matrix.put(0, 3, -transMat.m03);
					matrix.put(1, 3, -transMat.m13);
					matrix.put(2, 3, transMat.m23);
					matrix.put(3, 3, 1);
						
	//				System.out.println(matrix.toString());
	//				System.out.println("Found Marker!");
					
					calcEulerRots();
					
					return matrix;

				}else{
					System.out.println("No Marker Found!");
				}
			} 
		} catch (NyARException e) {
			e.printStackTrace();
		}
		
		 return null;
	}
}
