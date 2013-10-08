package localisation;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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


public class AxisLocator {

	private static NyARBufferedImageRaster raster;
		IplImage image;
	  private NyARSingleDetectMarker detector;

	  private NyARDoubleMatrix44 transMat = new NyARDoubleMatrix44();

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

					boolean foundMarker = detector.detectMarkerLite(raster, 120);
					
					if (foundMarker) {
						detector.getTransmat(transMat);
						System.out.println("Matrixing.");
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

		
						System.out.println(matrix.toString());

					}else{
						System.out.println("No Marker Found!");
					}
				} 
			} catch (NyARException e) {
				e.printStackTrace();
			}
      
		 }  // end of processStimulus()
	

}
