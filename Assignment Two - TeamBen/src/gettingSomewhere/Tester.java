package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;

import colorCalibration.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Tester {

	public static void main(String[] args) {
		
		
		IplImage sourceImageRGB = cvLoadImage("test_images/colorchart.png");
	//	cvShowImage("orig.", sourceImageRGB);
		ColorChart chart = new ColorChart(sourceImageRGB);
		chart.findCalibColors();
		
	//	FindCorners corners = new FindCorners(sourceImage);
	//	corners.findObject();
	}

}

