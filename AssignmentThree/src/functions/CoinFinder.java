package functions;

import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_LU;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvInv;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.TreeMap;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class is used to identify coins within a rectified image.
 * After find and determineValues have been run, functions give access
 * to a variety of data including depth, radius (in pixels and mm),
 * position (in rectified image and original image) and value.
 *
 */

public class CoinFinder {
	
	private IplImage sourceImage;
	private IplImage coinsDrawn;
	
	private ArrayList<Float> plateCoord;
	private ArrayList<Integer> plateRadius;
	
	private ArrayList<Integer> values;
	private ArrayList<TreeMap<Integer, CvPoint>> goldCoinData = new ArrayList<TreeMap<Integer, CvPoint>>();
	private ArrayList<TreeMap<Integer, CvPoint>> silverCoinData = new ArrayList<TreeMap<Integer, CvPoint>>();
	
	// TreeMap < value, point in original image (x,y) >
	private ArrayList<TreeMap<Double, ArrayList<Double>>> coinLocationData = new ArrayList<TreeMap<Double, ArrayList<Double>>>();
	
	private double height;
	private int minCoinRadius;
	private int maxCoinRadius;
	
	private double pixelSize;
	
	public CoinFinder(IplImage sourceImage, double height) {
		this.sourceImage = sourceImage;
		// height is in mm
		this.height = height;
		
		// assumes perfectly stright on view, so only considers width
		double xangle = 34.5*Math.PI/180.0;
		
		double physFrameWidth = height*Math.tan(xangle);
		pixelSize = physFrameWidth/((double)sourceImage.width()); // mm/pix
		
		//System.out.println("height: "+height);
		//System.out.println("physical width: "+physFrameWidth);
		
		minCoinRadius = (int) (9.0/pixelSize); // pix
		maxCoinRadius = (int) (22.0/pixelSize); // pix
		
		//System.out.println("minCoinRadius: "+minCoinRadius);
		//System.out.println("maxCoinRadius: "+maxCoinRadius);
	}
	
	// gives list of:  value --> point in original image (x,y)
	public ArrayList<TreeMap<Double, ArrayList<Double>>> getCoinLocationData() {
		return coinLocationData;
	}
	
	// arranged in increasing value in $
	public ArrayList<Integer> getValues() {
		return values;
	}
	
	public Double getTotalValue() {
		double total = 0;
		total += values.get(0)*0.05;
		total += values.get(1)*0.10;
		total += values.get(2)*0.20;
		total += values.get(3)*0.50;
		total += values.get(4)*1.00;
		total += values.get(5)*2.00;
		return total;
	}
	
	public double getPixelSize() {
		return pixelSize;
	}
	
	public void find() {
		
		plateCoord = new ArrayList<Float>();
		plateRadius = new ArrayList<Integer>();
		plateCoord.add(0, (float) (((double)sourceImage.width())/2.0));
		plateCoord.add((float) (((double)sourceImage.height())/2.0));
		plateRadius.add(0, (int)(((double)sourceImage.height())/2.0)-1);
		
		HoughCircles circles = new HoughCircles(sourceImage.clone(), minCoinRadius, maxCoinRadius);
		circles.runHoughCirclesRGBCoins();
		
		//circles.display("circles found");
		
		ArrayList<Float> coordList = circles.getCircleDataList();
		ArrayList<Integer> radiusList = circles.getRadiusDataList();
		
		for (int i=0; i < coordList.size()-1; i+=2) {
			if (coordList.size() == 0) {
				break;
			}
			Float x = coordList.get(i);
			Float y = coordList.get(i+1);
			if ((Math.pow(x-plateCoord.get(0),2)+Math.pow(y-plateCoord.get(1),2))
					> Math.pow(plateRadius.get(0), 2)) {
				coordList.remove(i);
				// i shuffled left, hence i=i+1
				coordList.remove(i);
				radiusList.remove(i/2);
				i-=2;
			}
		}
		
		
		ColorDetector colorMod = new ColorDetector(sourceImage.clone());
		coinsDrawn = sourceImage.clone();
		
		colorMod.hsvThresholdGold();
		//colorMod.display();
		
		Integer goldCount = 0;
		int k=0;
		for (int i=0; i < radiusList.size(); i++) {
			if (coordList.size() == 0) {
				break;
			}
			Float x = coordList.get(k);
			Float y = coordList.get(k+1);
			k+=2;
			Integer radius = radiusList.get(i);
			boolean isGold = isMostlyWhite(colorMod.getThresholdImage(), x.intValue(), y.intValue(), radius);
			if (isGold) {
				goldCount++;
				CvPoint center = cvPointFrom32f(new CvPoint2D32f(x, y));
				cvCircle(coinsDrawn, center, radius, CvScalar.YELLOW, 1, CV_AA, 0);
				TreeMap<Integer, CvPoint> newmap = new TreeMap<Integer, CvPoint>();
				newmap.put(radius, center);
				goldCoinData.add(newmap);
			}
		}
		
		colorMod.hsvThresholdSilver();
		//colorMod.display();
		
		Integer silverCount = 0;
		k=0;
		for (int i=0; i < radiusList.size(); i++) {
			if (coordList.size() == 0) {
				break;
			}
			Float x = coordList.get(k);
			Float y = coordList.get(k+1);
			k+=2;
			Integer radius = radiusList.get(i);
			boolean isSilver = isMostlyWhite(colorMod.getThresholdImage(), x.intValue(), y.intValue(), radius);
			if (isSilver) {
				silverCount++;
				CvPoint center = cvPointFrom32f(new CvPoint2D32f(x, y));
				cvCircle(coinsDrawn, center, radius, CvScalar.WHITE, 1, CV_AA, 0);
				TreeMap<Integer, CvPoint> newmap = new TreeMap<Integer, CvPoint>();
				newmap.put(radius, center);
				silverCoinData.add(newmap);
			}
		}
	}
	
	public void determineValues() {
		// {5c, 10c, 20c, 50c, 1aud, 2aud}
		values = new ArrayList<Integer>();
		for (int i=0; i < 6; i++) {
			values.add(0);
		}
		
		// GOLD COINS
		Double maxrad = 0.0; Double minrad = 50.0;
		for (TreeMap<Integer, CvPoint> thiscoin : goldCoinData) {
			Double radiusmm = ((double)thiscoin.firstKey())*pixelSize;
			if (radiusmm.compareTo(maxrad) > 0) {
				maxrad = radiusmm;
			} else if (radiusmm.compareTo(minrad) < 0) {
				minrad = radiusmm;
			}
		}
		Double midpoint = (maxrad+minrad)/2.0;
		
		// compare to midpoint to determine value
		/**
		if (maxrad-minrad > 2) {
			for (TreeMap<Integer, CvPoint> thiscoin : goldCoinData) {
				Double radiusmm = ((double)thiscoin.firstKey())*pixelSize;		// pix*mm/pix
				// if $1
				if (radiusmm.compareTo(midpoint) > 0 || radiusmm.compareTo(midpoint) == 0) {
					values.set(4, values.get(4)+1);
					CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
					addNewData(1.0, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
				}
				// if $2
				else {
					values.set(5, values.get(5)+1);
					CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
					addNewData(2.0, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
				}
			}
		}
		*/
			for (TreeMap<Integer, CvPoint> thiscoin : goldCoinData) {
				Double radiusmm = ((double)thiscoin.firstKey())*pixelSize;
				// if $1
				if (radiusmm.compareTo(8.0) > 0 || radiusmm.compareTo(11.0) < 0) {
					values.set(4, values.get(4)+1);
					CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
					addNewData(1.0, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
				}
				// if $2
				else if (radiusmm.compareTo(11.0) > 0 || radiusmm.compareTo(14.0) < 0){
					values.set(5, values.get(5)+1);
					CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
					addNewData(2.0, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
				}
			}
		
		
		// SILVER COINS
		for (TreeMap<Integer, CvPoint> thiscoin : silverCoinData) {
			Double radiusmm = ((double)thiscoin.firstKey())*pixelSize;
			// if 5c
			if (radiusmm.compareTo(11.6) <0) {
				values.set(0, values.get(0)+1);
				CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
				addNewData(0.05, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
			}
			// if 10c
			else if (radiusmm.compareTo(13.0) < 0) {
				values.set(1, values.get(1)+1);
				CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
				addNewData(0.1, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
			}
			// if 20c
			else if (radiusmm.compareTo(15.6) < 0) {
				values.set(2, values.get(2)+1);
				CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
				addNewData(0.2, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
			}
			// if 50c
			else {
				values.set(3, values.get(3)+1);
				CvPoint pixLocation = thiscoin.get(thiscoin.firstKey());
				addNewData(0.5, ((double)pixLocation.x())*pixelSize, ((double)pixLocation.y())*pixelSize);
			}
		}
	}
	
	// maps of pixel radius to location in rectified image
	public ArrayList<TreeMap<Integer, CvPoint>> getGoldCoinData() {
		return goldCoinData;
	}
	
	// maps of pixel radius to location in rectified image
	public ArrayList<TreeMap<Integer, CvPoint>> getSilverCoinData() {
		return silverCoinData;
	}
	
	public IplImage getDrawnCoins() {
		return coinsDrawn.clone();
	}
	
	// use on a thresholded image to determine if a circle is mostly within this threshold
	// this allows for outliers and improves robustness of color thresholding
	private static boolean isMostlyWhite(IplImage thresholdImage, Integer x, Integer y, Integer radius) {
		Integer whitePixels = 0; Integer blackPixels = 0;
		for (int i = x-radius; i < x+radius; i++) {
			for (int j = y-radius; j < y+radius; j++) {
				if (i <= 0 || i >= thresholdImage.width() || j <= 0 || j >= thresholdImage.height()) {
					continue;
				}
				ArrayList<Double> colorData = getPixelColor(thresholdImage, i, j);
				if (colorData.get(0).compareTo(255.0) == 0) {
					whitePixels++;
				}
				else {
					blackPixels++;
				}
			}
		}
		// take into account black pixels outside circle
		blackPixels -= (int)(0.22*Math.pow(2*radius, 2));
		return whitePixels.compareTo(blackPixels) > 0;
	}
	
	private static ArrayList<Double> getPixelColor(IplImage image, int x, int y) {
		CvScalar s=cvGet2D(image, y, x);                
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(s.val(0)); values.add(s.val(1)); values.add(s.val(2));
		return values;
	}
	
	private void addNewData(double val, double x, double y) {
		ArrayList<Double> trans = new ArrayList<Double>();
		trans.add(x); trans.add(y);
		TreeMap<Double, ArrayList<Double>> newmap = new TreeMap<Double, ArrayList<Double>>();
		newmap.put(val, trans);
		coinLocationData.add(newmap);
	}
}
