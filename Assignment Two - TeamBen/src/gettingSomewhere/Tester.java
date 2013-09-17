package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import colorCalibration.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Tester {

	public static void main(String[] args) {
		
		IplImage sourceImageRGB = cvLoadImage("test_images/color_checker.png");
		ColorChart chart = new ColorChart(sourceImageRGB);
		//chart.doStuff();
		chart.findCalibColors();
		
	//	FindCorners corners = new FindCorners(sourceImage);
	//	corners.findObject();
	}

}

