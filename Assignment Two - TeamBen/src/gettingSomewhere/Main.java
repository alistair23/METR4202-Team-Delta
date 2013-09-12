package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import functions.*;

public class Main {

	public static void main(String[] args) {
		
		IplImage sourceImage = cvLoadImage(args[0]);
		
		HoughCircles circles = new HoughCircles(sourceImage.clone());
		circles.runHoughCirclesRGB();
		//printCoordinates(circles.getCircleDataList());
		ArrayList<Float> coordList = circles.getCircleDataList();
		//printCoordinates(coordList);
		circles.display("Preliminary Circles");
		
		ColorDetector colorMod = new ColorDetector(sourceImage.clone());
		colorMod.hsvThresholdCoord(coordList.get(0).intValue(), coordList.get(1).intValue());
		//colorMod.display();
		
		HoughCircles goldCircles = new HoughCircles(colorMod.getThresholdImage());
		goldCircles.runHoughCirclesHSV();
		goldCircles.display("Gold Circles");
	}
	
	private static void printCoordinates(ArrayList<Float> coordList) {
		for(int i=0; i < coordList.size(); i+=2) {
			System.out.print(coordList.get(i).toString()+", ");
			System.out.println(coordList.get(i+1));
		}
	}
}
