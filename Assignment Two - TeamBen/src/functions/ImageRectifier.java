package functions;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2YCrCb;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import java.util.ArrayList;
import java.util.TreeMap;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageRectifier {
	
	private static IplImage sourceImage;
	private static IplImage depthImage;
	private static ArrayList<Integer> depthPickerData;
	private static TreeMap<Integer, Integer> depthData;
	
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
		CvSize imgSize = cvGetSize(sourceImage);
		//cvShowImage("source", sourceImage);
		//cvShowImage("depth", depthImage);
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
		
		//cvShowImage("depth", toShow);
		
		ArrayList<Double> datafit = fitToData();
		double slope = datafit.get(0);
		double intercept = datafit.get(1);
		
		IplImage toShowFit = sourceImage.clone();
		int imgwidth = toShowFit.width();
		int imgheight = toShowFit.height();
		
		double[] vals = new double[2];
		vals[0] = 100; vals[1] = 200;
		
		int pix0 = (int) ((vals[0]-intercept)/slope);
		int pix1 = (int) ((vals[1]-intercept)/slope);
		
		//double ratio = ((double)pix0)/((double)pix1);
		double ratio = vals[0]/vals[1];
		System.out.println(ratio);
		
		int blerg = (int)(200/ratio)/2;
		
		CvPoint pt1 = new CvPoint(imgwidth/2-100, pix0); CvPoint pt2 = new CvPoint(imgwidth/2+100, pix0);
		cvLine(toShowFit, pt1, pt2, CvScalar.RED, 2, CV_AA, 0);
		
		pt1 = new CvPoint((int)(imgwidth/2-blerg), pix1); pt2 = new CvPoint((int)(imgwidth/2+blerg), pix1);
		cvLine(toShowFit, pt1, pt2, CvScalar.RED, 2, CV_AA, 0);
		
		int deltaPix = (int) ((vals[0]-intercept)/slope-(vals[1]-intercept)/slope);
		System.out.println(deltaPix);
		
		//cvShowImage("depth fit", toShowFit);
		//cvWaitKey(0);
		
		return toShowFit;
	}
	
	private static double getPixelValue(IplImage rgbImage, int x, int y) {
		IplImage hsvImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, hsvImage, CV_RGB2HSV);

		return cvGet2D(hsvImage, y, x).getVal(2);
	}
	
	private static ArrayList<Double> fitToData() {
		ArrayList<Double> fit = new ArrayList<Double>();
		
		//int n = 0;
/**		// x is pixel, y is value
		double[] x = new double[1000]; double[] y = new double[1000];
		
		int pos = 0;
		for (int pix : depthData.keySet()) {
			x[pos] = pix;
			y[pos] = depthData.get(pix);
			pos++;
		}
		
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (n = 0; n < x.length; n++) {
            sumx  += x[n];
            sumx2 += x[n] * x[n];
            sumy  += y[n];
        }
        
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        System.out.println("y   = " + beta1 + " * x + " + beta0);
        fit.add(beta1); fit.add(beta0);
        
        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        double R2    = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/n + xbar*xbar*svar1;
        System.out.println("R^2                 = " + R2);
        System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
        svar0 = svar * sumx2 / (n * xxbar);
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

        System.out.println("SSTO = " + yybar);
        System.out.println("SSE  = " + rss);
        System.out.println("SSR  = " + ssr);
*/		
		
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
