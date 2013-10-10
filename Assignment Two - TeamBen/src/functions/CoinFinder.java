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
	private IplImage depthImage;
	private IplImage coinsDrawn;
	
	private ArrayList<Float> plateCoord;
	private ArrayList<Integer> plateRadius;
	
	private double[] depth_lookup = new double[2048];
	
	private ArrayList<Integer> values;
	private ArrayList<TreeMap<Integer, CvPoint>> goldCoinData = new ArrayList<TreeMap<Integer, CvPoint>>();
	private ArrayList<TreeMap<Integer, CvPoint>> silverCoinData = new ArrayList<TreeMap<Integer, CvPoint>>();
	
	private CvMat invRectMat;
	
	// TreeMap < value, point in original image (x,y,z) >
	private ArrayList<TreeMap<Double, ArrayList<Double>>> coinLocationData = new ArrayList<TreeMap<Double, ArrayList<Double>>>();
	
	public CoinFinder(IplImage sourceImage, IplImage depthImage, CvMat rectMat) {
		this.sourceImage = sourceImage;
		this.depthImage = depthImage;
		
		invRectMat = cvCreateMat(3,3,CV_32FC1);
		cvInv(rectMat, invRectMat, CV_LU);

		double k1 = 1.1863; double k2 = 2842.5; double k3 = 0.1236;
		for (int i=0; i<2048; i++) {
			double depth = k3 * Math.tan(i/k2 + k1);
			depth_lookup[i] = depth;
		}
	}
	
	// gives list of:  value --> point in original image (x,y,z)
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
	
	public void find() {
		
		HoughCircles plate = new HoughCircles(sourceImage.clone());
		plate.runHoughCirclesRGBPlate();
		plateCoord = plate.getCircleDataList();
		plateRadius = plate.getRadiusDataList();
		
		if (plateCoord.size() == 0 || plateRadius.size() == 0) {
			plateCoord.add(0, (float) (640.0/2.0)); plateCoord.add((float) (480.0/2.0));
			plateRadius.add(0, 200);
		}
		
		HoughCircles circles = new HoughCircles(sourceImage.clone());
		circles.runHoughCirclesRGBCoins();
		
		ArrayList<Float> coordList = circles.getCircleDataList();
		ArrayList<Integer> radiusList = circles.getRadiusDataList();
		
		for (int i=0; i < coordList.size()-1; i+=2) {
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
		
		IplImage tempImage = sourceImage.clone();
		int j=0;
		for (int i=0; i < radiusList.size(); i++) {
			float x = coordList.get(j);
			float y = coordList.get(j+1);
			j+=2;
			int radius = radiusList.get(i);
			CvPoint center = cvPointFrom32f(new CvPoint2D32f(x, y));
			cvCircle(tempImage, center, radius, CvScalar.RED, 1, CV_AA, 0);
		}
		
		ColorDetector colorMod = new ColorDetector(sourceImage.clone());
		coinsDrawn = sourceImage.clone();
		
		colorMod.hsvThresholdGold();
		Integer goldCount = 0;
		int k=0;
		for (int i=0; i < radiusList.size(); i++) {
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
		Integer silverCount = 0;
		k=0;
		for (int i=0; i < radiusList.size(); i++) {
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
		
		// gold coins
		ArrayList<ArrayList<Object>> goldCoinsMm = new ArrayList<ArrayList<Object>>();
		for (TreeMap<Integer, CvPoint> coin : goldCoinData) {
			Integer radius = coin.firstKey();
			CvPoint point = coin.get(radius);
			
			// find distance pixel value average
			int platerad = plateRadius.get(0);
			int pointx = point.x(); int pointy = point.y();
			int avgpix = 0;
			for (int i = -platerad; i < platerad; i++) {
				int x = pointx+i;
				Double pixelInfo = getPixelColor(depthImage, x, pointy).get(2);
				if (pixelInfo != 0.0) {
					avgpix += pixelInfo;
				}
			}
			avgpix = (int) (((double)avgpix)/((double)(2*platerad)));
			
			// find corresponding physical depth
			double physDepth = depth_lookup[avgpix*2]*1000+40;
			// calculate physical field of view width at that depth
			double physFrameWidth = 2.0*physDepth*Math.tan(28.5*(Math.PI/180.0));
			// calculate corresponding mm width to each pixel at that depth
			double pixWidth = physFrameWidth/((double)sourceImage.width());
			// find physical coin radius
			double coinRadiusmm = pixWidth*radius.doubleValue()*2.0;
			// calculate physical field of view height at that depth
			double physFrameHeight = 2.0*physDepth*Math.tan(21.5*(Math.PI/180.0));
			// calculate corresponding mm height to each pixel at that depth
			double pixHeight = physFrameHeight/((double)sourceImage.height());
			
			ArrayList<Object> newlist= new ArrayList<Object>();
			newlist.add(coinRadiusmm); newlist.add(physDepth); newlist.add(point);
			newlist.add(pixWidth); newlist.add(pixHeight);
			goldCoinsMm.add(newlist);
		}
		
		Double maxrad = 0.0; Double minrad = 50.0;
		for (ArrayList<Object> thislist : goldCoinsMm) {
			Double radiusmm = (Double)thislist.get(0);
			if (radiusmm.compareTo(maxrad) > 0) {
				maxrad = radiusmm;
			} else if (radiusmm.compareTo(minrad) < 0) {
				minrad = radiusmm;
			}
		}
		Double midpoint = (maxrad+minrad)/2.0;
		
		// compare to midpoint to determine value
		if (maxrad-minrad > 2) {
			for (ArrayList<Object> thislist : goldCoinsMm) {
				Double radiusmm = (Double)thislist.get(0);
				// if $1
				if (radiusmm.compareTo(midpoint) > 0 || radiusmm.compareTo(midpoint) == 0) {
					values.set(4, values.get(4)+1);
					// transform point in rectified image back to the original frame
					CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
					addNewData(1.0, (transPoint.x()-320)*(double)thislist.get(3),
							(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
				}
				// if $2
				else {
					values.set(5, values.get(5)+1);
					// transform point in rectified image back to the original frame
					CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
					addNewData(2.0, (transPoint.x()-320)*(double)thislist.get(3),
							(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
				}
			}
		}
		// if only one value of coin compare to physical radius
		else {
			for (ArrayList<Object> thislist : goldCoinsMm) {
				Double radiusmm = (Double)thislist.get(0);
				// if $1
				if (radiusmm.compareTo(20.0) > 0 || radiusmm.compareTo(20.0) == 0) {
					values.set(4, values.get(4)+1);
					// transform point in rectified image back to the original frame
					CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
					addNewData(1.0, (transPoint.x()-320)*(double)thislist.get(3),
							(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
				}
				// if $2
				else {
					values.set(5, values.get(5)+1);
					// transform point in rectified image back to the original frame
					CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
					addNewData(2.0, (transPoint.x()-320)*(double)thislist.get(3),
							(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
				}
			}
		}
		
		
		// silver coins
		ArrayList<ArrayList<Object>> silverCoinsMm = new ArrayList<ArrayList<Object>>();
		for (TreeMap<Integer, CvPoint> coin : silverCoinData) {
			Integer radius = coin.firstKey();
			CvPoint point = coin.get(radius);
			
			int platerad = plateRadius.get(0);
			int pointx = point.x(); int pointy = point.y();
			int avgpix = 0;
			for (int i = -platerad; i < platerad; i++) {
				int x = pointx+i;
				Double pixelInfo = getPixelColor(depthImage, x, pointy).get(2);
				if (pixelInfo != 0.0) {
					avgpix += pixelInfo;
				}
			}
			avgpix = (int) (((double)avgpix)/((double)(2*platerad)));
			
			// find corresponding physical depth
			double physDepth = depth_lookup[avgpix*2]*1000+40;
			// calculate physical field of view width at that depth
			double physFrameWidth = 2.0*physDepth*Math.tan(28.5*(Math.PI/180.0));
			// calculate corresponding mm width to each pixel at that depth
			double pixWidth = physFrameWidth/((double)sourceImage.width());
			// find physical coin radius
			double coinRadiusmm = pixWidth*radius.doubleValue()*2.0;
			// calculate physical field of view height at that depth
			double physFrameHeight = 2.0*physDepth*Math.tan(21.5*(Math.PI/180.0));
			// calculate corresponding mm height to each pixel at that depth
			double pixHeight = physFrameHeight/((double)sourceImage.height());
			
			ArrayList<Object> newlist= new ArrayList<Object>();
			newlist.add(coinRadiusmm); newlist.add(physDepth); newlist.add(point);
			newlist.add(pixWidth); newlist.add(pixHeight);
			silverCoinsMm.add(newlist);
		}
		
		for (ArrayList<Object> thislist : silverCoinsMm) {
			Double radiusmm = (Double)thislist.get(0);
			// if 5c
			if (radiusmm.compareTo(20.4) <0) {
				values.set(0, values.get(0)+1);
				// transform point in rectified image back to the original frame
				CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
				addNewData(0.05, (transPoint.x()-320)*(double)thislist.get(3),
						(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
			}
			// if 10c
			else if (radiusmm.compareTo(24.6) < 0) {
				values.set(1, values.get(1)+1);
				// transform point in rectified image back to the original frame
				CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
				addNewData(0.1, (transPoint.x()-320)*(double)thislist.get(3),
						(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
			}
			// if 20c
			else if (radiusmm.compareTo(29.6) < 0) {
				values.set(2, values.get(2)+1);
				// transform point in rectified image back to the original frame
				CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
				addNewData(0.2, (transPoint.x()-320)*(double)thislist.get(3),
						(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
			}
			// if 50c
			else {
				values.set(3, values.get(3)+1);
				// transform point in rectified image back to the original frame
				CvPoint transPoint = transformBack((CvPoint)thislist.get(2));
				addNewData(0.5, (transPoint.x()-320)*(double)thislist.get(3),
						(transPoint.y()-240)*(double)thislist.get(4), (Double)thislist.get(1));
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
	
	// Transforms a  point in the rectified image back to the original frame
	// Simplest way to achieve this is to draw a cross on a blank image then recognise this after transformation.
	// Trying to find a more direct approach...
	private CvPoint transformBack(CvPoint point) {
		BufferedImage img = new BufferedImage(sourceImage.width(), sourceImage.height(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = img.getRaster();
        int[] pix = new int[1]; pix[0] = 255;
        for (int y=point.y()-10; y <= point.y()+10; y++) {
	        for (int x=point.x()-10; x <= point.x()+10; x++) {
	        	wr.setPixel(x, y, pix);
	        }
        }
		
        IplImage newimg = IplImage.createFrom(img);
        IplImage im_out =  cvCreateImage(cvSize(sourceImage.width(), sourceImage.height()), IPL_DEPTH_8U, 1);
		cvWarpPerspective(newimg, im_out, invRectMat, CV_INTER_LINEAR, CvScalar.ZERO);
		
		ArrayList<Integer> newxs = new ArrayList<Integer>();
		ArrayList<Integer> newys = new ArrayList<Integer>();
        for (int y=200; y < sourceImage.height()-100; y++) {
        	for (int x=100; x < sourceImage.width()-100; x++) {
        		if ((int) cvGet2D(im_out, y, x).val(0) != 0) {
        			newxs.add(x); newys.add(y);
        		}
            }
        }
        int xmin = 1000; int xmax = 0;
        for (int x : newxs) {
        	if (x < xmin) {
        		xmin = x;
        	} if (x > xmax) {
        		xmax = x;
        	}
        }
        int ymin = 1000; int ymax = 0;
        for (int y : newys) {
        	if (y < ymin) {
        		ymin = y;
        	} if (y > ymax) {
        		ymax = y;
        	}
        }
        
        double newx = ((double)(xmax-xmin))/2.0 + xmin;
        double newy = ((double)(ymax-ymin))/2.0 + ymin;
        
        CvPoint transPoint = cvPointFrom32f(new CvPoint2D32f(newx, newy));
       return transPoint;
	}
	
	private void addNewData(double val, double x, double y, double z) {
		ArrayList<Double> trans = new ArrayList<Double>();
		trans.add(x); trans.add(y); trans.add(z);
		TreeMap<Double, ArrayList<Double>> newmap = new TreeMap<Double, ArrayList<Double>>();
		newmap.put(val, trans);
		coinLocationData.add(newmap);
	}
}
