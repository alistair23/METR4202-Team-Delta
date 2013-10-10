package functions;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.util.ArrayList;
import java.util.TreeMap;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class is used to rectify an image to be face-on to a flat surface.
 * The class relies on the fact that it can 150 pixels of consistant data to linearise.
 * If the Kinect is too close to obtain usable (non-zero) depth data this class becomes unreliable.
 * 
 * Call drawTableLines() prior to transformImage().
 * 
 */

public class ImageRectifier {
	
	private static IplImage sourceImage;
	private static IplImage depthImage;
	private static ArrayList<Integer> depthPickerData;
	private static TreeMap<Integer, Integer> depthData;
	private static IplImage tableDrawn;
	
	private static CvPoint pt1;
	private static CvPoint pt2;
	private static CvPoint pt3;
	private static CvPoint pt4;
	
	private static int pix0, pix1, delta;
	
	private CvMat mmat;
	private CvPoint2D32f c1;
	private CvPoint2D32f c2;
	
	public ImageRectifier(IplImage sourceImage, IplImage depthImage) {
		this.sourceImage = sourceImage.clone();
		this.depthImage = depthImage.clone();
		
		int gap = 0;
		int x = 0; int y = 0;
		for (x = depthImage.width()/2; x < depthImage.width(); x++) {
			gap = 0;
			for (y=100; y < depthImage.height(); y++) {
				if (getPixelValue(depthImage, x, y)==0) {
					break;
				}
				gap++;
			}
			if (gap >= 150) {
				ArrayList<Integer> coords = new ArrayList<Integer>();
				coords.add(x-10); coords.add(100);
				coords.add(20); coords.add(y-100);
				this.depthPickerData = coords;
				break;
			}
		}
	}
	
	public TreeMap<Integer, Integer> getDepthData() {
		return depthData;
	}
	
	public  IplImage drawTableLines() {
		
		int xMin = depthPickerData.get(0); int width = depthPickerData.get(2);
		int yMin = depthPickerData.get(1); int height = depthPickerData.get(3);
		
		depthData = new TreeMap<Integer, Integer>();
		IplImage toShow = sourceImage.clone();
		
		int x = xMin+width/2;
		int y_bottom = yMin;
		int y_top = yMin + height;
		
		double prevPixelValue = 255.0;
		
		for (int i = y_bottom; i < y_top; i++) {
			double a = getPixelValue(depthImage, x, i);
			double b = getPixelValue(depthImage, x+2, i);
			double c = getPixelValue(depthImage, x+5, i);
			double d = getPixelValue(depthImage, x-2, i);
			double e = getPixelValue(depthImage, x-5, i);
			double pixelValue = (a+b+c+d+e)/5;
			
			if ((pixelValue%5 == 0.0) && (pixelValue != prevPixelValue) &&
					(pixelValue != 255.0) && ! depthData.containsValue(pixelValue)) {
				
				prevPixelValue = pixelValue;
				CvPoint pt1 = new CvPoint(x-width/2, i); CvPoint pt2 = new CvPoint(x+width/2, i);
				cvLine(toShow, pt1, pt2, CvScalar.RED, 2, CV_AA, 0);
				depthData.put(i, (int)pixelValue);
			}
		}
		
		ArrayList<Double> datafit = fitToData();
		double slope = datafit.get(0);
		double intercept = datafit.get(1);
		
		tableDrawn = sourceImage.clone();
		int imgwidth = tableDrawn.width();
		int imgheight = tableDrawn.height();
		
		double[] vals = new double[2];
		vals[0] = 100; vals[1] = 200;
		
		pix0 = (int) ((vals[0]-intercept)/slope);
		pix1 = (int) ((vals[1]-intercept)/slope);
		
		double ratio = (vals[0]/vals[1])*1.5;
		
		delta = (int)(200/ratio)/2;
		
		pt1 = new CvPoint(imgwidth/2-100, pix0);
		pt2 = new CvPoint(imgwidth/2+100, pix0);
		cvLine(tableDrawn, pt1, pt2, CvScalar.RED, 2, CV_AA, 0);
		
		pt3 = new CvPoint((int)(imgwidth/2-delta), pix1);
		pt4 = new CvPoint((int)(imgwidth/2+delta), pix1);
		cvLine(tableDrawn, pt3, pt4, CvScalar.RED, 2, CV_AA, 0);
		
		cvLine(tableDrawn, pt1, pt3, CvScalar.RED, 2, CV_AA, 0);
		cvLine(tableDrawn, pt2, pt4, CvScalar.RED, 2, CV_AA, 0);
		
		return tableDrawn;
	}
	
	public IplImage transformImage() {
		
		int imgwidth = sourceImage.width();
		int imgheight = sourceImage.height();
		
		mmat = cvCreateMat(3,3,CV_32FC1);
	    c1 = new CvPoint2D32f(4);
	    c2 = new CvPoint2D32f(4);

	    //corner points from drawTableLines
	    c1.position(0).put(pt1);
	    c1.position(1).put(pt2);
	    c1.position(2).put(pt3);
	    c1.position(3).put(pt4);
	    
	    double wtoh = (((double)delta)/1.5)/((double)imgwidth);
	    int hdelta = (int) (wtoh*((double)imgheight));
	    
	    CvPoint topt1 = new CvPoint((int)(imgwidth/2-delta), pix0);
	    CvPoint topt2 = new CvPoint((int)(imgwidth/2+delta), pix0);
	    CvPoint topt3 = new CvPoint((int)(imgwidth/2-delta), pix1+hdelta);
	    CvPoint topt4 = new CvPoint((int)(imgwidth/2+delta), pix1+hdelta);
	    
	    c2.position(0).put(topt1);
	    c2.position(1).put(topt2);
	    c2.position(2).put(topt3);
	    c2.position(3).put(topt4);
	    
	    c1.position(0); c2.position(0);
	    cvGetPerspectiveTransform(c1, c2, mmat);
	    
	    IplImage im_out =  cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, sourceImage.nChannels());
	    
	    cvWarpPerspective(sourceImage, im_out, mmat, CV_INTER_LINEAR, CvScalar.ZERO);
	    
	    IplImage threechannel = cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, 3);
	    cvCvtColor(im_out, threechannel, CV_RGBA2RGB);
	    
	    return threechannel;
	}
	
	public IplImage transformDepthImage() {
		int imgwidth = sourceImage.width();
		int imgheight = sourceImage.height();
		
	    IplImage im_out =  cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, depthImage.nChannels());
	    
	    cvWarpPerspective(depthImage, im_out, mmat, CV_INTER_LINEAR, CvScalar.ZERO);
	    
	    IplImage threechannel = cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, 3);
	    cvCvtColor(im_out, threechannel, CV_RGBA2RGB);
	    
	    return threechannel;
	}
	
	public IplImage reverseTransform(IplImage inImage)  {
		int imgwidth = inImage.width();
		int imgheight = inImage.height();
		
		CvMat invmat = cvCreateMat(3,3,CV_32FC1);
		cvInv(mmat, invmat, CV_LU);
		
		IplImage im_out =  cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, inImage.nChannels());
		cvWarpPerspective(inImage, im_out, invmat, CV_INTER_LINEAR, CvScalar.ZERO);
		
		return im_out;
	}
	
	private static double getPixelValue(IplImage rgbImage, int x, int y) {
		return cvGet2D(rgbImage, y, x).getVal(2);
	}
	
	private static ArrayList<Double> fitToData() {
		ArrayList<Double> fit = new ArrayList<Double>();	
		
		int sumx = 0, sumy = 0;
		double sumxy = 0.0, sumx2 = 0.0;
		int n = depthData.size();
		
		for (int x : depthData.keySet()) {
			int y = depthData.get(x);
			sumx += x; sumy += y;
			sumxy += x*y; sumx2 += Math.pow(x, 2);
		}
		
		double slope = (n*sumxy-sumx*sumy)/(n*sumx2-sumx*sumx);
		double intercept = (sumy-slope*sumx)/n;
		
		fit.add(slope); fit.add(intercept);
        
		return fit;
	}
	
	public CvMat getMatrix() {
		return mmat;
	}
}
