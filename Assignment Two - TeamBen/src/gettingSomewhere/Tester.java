package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import colorCalibration.ColorChart;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Tester {

	public static void main(String[] args) {
		
		//IplImage sourceImage = cvLoadImage("test_images/color_checker.jpg");
		IplImage sourceImage = cvLoadImage("test_images/calib.png");
		ColorChart chart = new ColorChart(sourceImage);
		chart.doStuff();
		

	}

}
