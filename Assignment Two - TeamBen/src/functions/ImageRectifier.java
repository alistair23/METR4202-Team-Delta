package functions;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2YCrCb;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.TreeMap;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

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
	
	public ImageRectifier(IplImage sourceImage, IplImage depthImage, ArrayList<Integer> depthPickerData) {
		this.sourceImage = sourceImage;
		this.depthImage = depthImage;
		this.depthPickerData = depthPickerData;
		
		//IplImage depthImageHSV = cvCreateImage(cvGetSize(depthImage),8,3);
		//cvCvtColor(depthImage, depthImageHSV, CV_RGB2HSV);
		
		//double a = cvGet2D(depthImageHSV, 300, 200).getVal(0);
		//double b = cvGet2D(depthImageHSV, 300, 200).getVal(1);
		//double c = cvGet2D(depthImageHSV, 300, 200).getVal(2);
		//double d = cvGet2D(depthImageHSV, 300, 200).getVal(3);
		//System.out.println(a+", "+b+", "+c);
		
		// 0.0, 109.0, 109.0
		// 0.0, 166.0, 166.0
		// 0.0, 165.0, 165.0
		
		//System.out.println(depthImage.nChannels());
		//System.out.println(depthImage.depth());
	}
	
	public TreeMap<Integer, Integer> getDepthData() {
		return depthData;
	}
	
	public static IplImage drawTableLines() {
		
		int xMin = depthPickerData.get(0); int width = depthPickerData.get(2);
		int yMin = depthPickerData.get(1); int height = depthPickerData.get(3);
		
		depthData = new TreeMap<Integer, Integer>();
		IplImage toShow = sourceImage.clone();
		//IplImage toShow = cvCreateImage(cvGetSize(depthImage),8,3);
		
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
			
			if ((pixelValue%5 == 0.0) && (pixelValue != prevPixelValue) && (pixelValue != 255.0) && ! depthData.containsValue(pixelValue)) {
				//System.out.print(i+" Value: "); System.out.println(pixelValue);
				prevPixelValue = pixelValue;
				CvPoint pt1 = new CvPoint(x-width/2, i); CvPoint pt2 = new CvPoint(x+width/2, i);
				//System.out.println(pt1); System.out.println(pt2);
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
		
		//double ratio = ((double)pix0)/((double)pix1);
		double ratio = (vals[0]/vals[1])*1.5;
		//System.out.println(ratio);
		//System.out.println(slope);
		
		delta = (int)(200/ratio)/2;
		
		pt1 = new CvPoint(imgwidth/2-100, pix0);
		pt2 = new CvPoint(imgwidth/2+100, pix0);
		cvLine(tableDrawn, pt1, pt2, CvScalar.RED, 2, CV_AA, 0);
		
		pt3 = new CvPoint((int)(imgwidth/2-delta), pix1);
		pt4 = new CvPoint((int)(imgwidth/2+delta), pix1);
		cvLine(tableDrawn, pt3, pt4, CvScalar.RED, 2, CV_AA, 0);
		
		cvLine(tableDrawn, pt1, pt3, CvScalar.RED, 2, CV_AA, 0);
		cvLine(tableDrawn, pt2, pt4, CvScalar.RED, 2, CV_AA, 0);
		
		//cvShowImage("drawn", tableDrawn);    
		
		return tableDrawn;
	}
	
	public IplImage transformImage() {
		
		int imgwidth = sourceImage.width();
		int imgheight = sourceImage.height();
		
		CvMat mmat = cvCreateMat(3,3,CV_32FC1);
	    CvPoint2D32f c1 = new CvPoint2D32f(4);
	    CvPoint2D32f c2 = new CvPoint2D32f(4);

	    //corner points of the parking place
	    c1.position(0).put(pt1);
	    c1.position(1).put(pt2);
	    c1.position(2).put(pt3);
	    c1.position(3).put(pt4);
	    
	    double wtoh = ((double)delta)/((double)imgwidth);
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
	    
	    //System.out.println(c1);
	    //System.out.println(c2);
	    //System.out.println(mmat);
	    
	    IplImage im_out =  cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, sourceImage.nChannels());
	    
	    cvWarpPerspective(sourceImage, im_out, mmat, CV_INTER_LINEAR, CvScalar.ZERO);
	    
	    IplImage threechannel = cvCreateImage(cvSize(imgwidth, imgheight), IPL_DEPTH_8U, 3);
	    cvCvtColor(im_out, threechannel, CV_RGBA2RGB);
	    
	    //cvShowImage("transformed", im_out);  
	    
	    return threechannel;
	}
	
	private static double getPixelValue(IplImage rgbImage, int x, int y) {
		IplImage hsvImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, hsvImage, CV_RGB2HSV);

		return cvGet2D(hsvImage, y, x).getVal(2);
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
        //System.out.println(fit);
        
		return fit;
	}
}
