package functions;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;

import java.util.ArrayList;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;
import functions.*;

public class CoinFinder {
	
	private IplImage sourceImage;
	
	public CoinFinder(IplImage sourceImage) {
		this.sourceImage = sourceImage;
	}
	
	public void find() {
		
		//IplImage orgImg = sourceImage.clone();
		//IplImage hsv = IplImage.create( orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels() );
		//IplImage hue = IplImage.create( orgImg.width(), orgImg.height(), orgImg.depth(), CV_8UC1 );
		//IplImage sat = IplImage.create( orgImg.width(), orgImg.height(), orgImg.depth(), CV_8UC1 );
		//IplImage val = IplImage.create( orgImg.width(), orgImg.height(), orgImg.depth(), CV_8UC1 );
		//cvCvtColor( orgImg, hsv, CV_BGR2HLS );
		//cvSplit( hsv, hue, sat, val, null );
		//cvShowImage("blehhhhh", val);  
		//cvWaitKey(0);
		
		//EdgesAndLines edgeTool = new EdgesAndLines(sourceImage);
		//IplImage edges = edgeTool.getEdges();
		//cvShowImage("Edges", edges);  
		//cvWaitKey(0);
		
		
		//sourceImage = IplImageUtils.equalizeHist(sourceImage,sourceImage);
		HoughCircles plate = new HoughCircles(sourceImage.clone());
		plate.runHoughCirclesRGBPlate();
		ArrayList<Float> plateCoord = plate.getCircleDataList();
		ArrayList<Integer> plateRadius = plate.getRadiusDataList();
		
		HoughCircles circles = new HoughCircles(sourceImage.clone());
		circles.runHoughCirclesRGBCoins();
		//printCoordinates(circles.getCircleDataList());
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
	//	System.out.println(coordList);
		
		cvShowImage("Identified Circles", tempImage);  
		cvWaitKey(0);
		
		//printCoordinates(coordList);
		//circles.display("Preliminary Circles");
		
		ColorDetector colorMod = new ColorDetector(sourceImage.clone());
		tempImage = sourceImage.clone();
	//	System.out.println("Check gold...");
		colorMod.hsvThresholdGold();
		colorMod.display();
		
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
				cvCircle(tempImage, center, radius, CvScalar.YELLOW, 1, CV_AA, 0);
			}
		}
	//	System.out.println("Check silver...");
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
				cvCircle(tempImage, center, radius, CvScalar.WHITE, 1, CV_AA, 0);
			}
		}
		cvShowImage("Coins Identified", tempImage);  
		cvWaitKey(0);
		
	//	System.out.print("Gold coins: "+goldCount.toString()+", ");
	//	System.out.println("Silver coins: "+silverCount.toString());
	}
	
	private static void printCoordinates(ArrayList<Float> coordList) {
		for(int i=0; i < coordList.size(); i+=2) {
	//		System.out.print(coordList.get(i).toString()+", ");
	//		System.out.println(coordList.get(i+1));
		}
	}
	
	private static boolean isMostlyWhite(IplImage thresholdImage, Integer x, Integer y, Integer radius) {
		Integer whitePixels = 0; Integer blackPixels = 0;
		for (int i = x-radius; i < x+radius; i++) {
			for (int j = y-radius; j < y+radius; j++) {
				ArrayList<Double> colorData = getPixelColor(thresholdImage, i, j);
				//System.out.println(colorData);
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
		
	//	System.out.print("White: "+whitePixels.toString()+", ");
	//	System.out.print("Black: "+blackPixels.toString()+", ");
	//	System.out.println("isMostlyWhite: "+(whitePixels.compareTo(blackPixels) > 0));
		return whitePixels.compareTo(blackPixels) > 0;
	}
	
	private static ArrayList<Double> getPixelColor(IplImage hsvImage, int x, int y) {
		CvScalar s=cvGet2D(hsvImage, y, x);                
		//System.out.println( "H:"+ s.val(0) + " S:" + s.val(1) + " V:" + s.val(2));//Print values
		
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(s.val(0)); values.add(s.val(1)); values.add(s.val(2));
		return values;
	}
}
