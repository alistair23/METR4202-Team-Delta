package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import colorCalibration.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import functions.ImageRectifier;

public class Tester {

	public static void main(String[] args) {
		
		
		IplImage colorchartsourceRGB = cvLoadImage("test_images/chart.png");
		//IplImage colorchartsourceRGB = cvLoadImage("test_images/colorchart.png");
	//	cvShowImage("original", colorchartsourceRGB);
		
		//IplImage hsv = IplImage.create(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, colorchartsourceRGB.nChannels());
		//IplImage hue = IplImage.create(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, 1);
		//IplImage sat = IplImage.create(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, 1);
		//IplImage val = IplImage.create(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, 1);
		//cvCvtColor(colorchartsourceRGB, hsv, CV_RGB2HSV);
		//cvShowImage("hsv", hsv);
		//cvSplit(hsv, hue, sat, val, null);
		//cvShowImage("hue", hue);
		//cvShowImage("sat", sat);
		//cvShowImage("val", val);
		//IplImage eq = cvCreateImage(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, 1);
		//cvEqualizeHist(val, eq);
		//eq = val;
		//cvShowImage("val", eq);
		//cvNormalize(sat, sat, 300, 0, 1, null);
		//IplImage newImage = cvCreateImage(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, 3);
		//cvMerge(hue, sat, eq, null, newImage);
		//IplImage toDisp = cvCreateImage(cvGetSize(colorchartsourceRGB), IPL_DEPTH_8U, 3);
		//cvCvtColor(newImage, toDisp, CV_HSV2RGB);
		
		IplImage blackimg = cvLoadImage("test_images/black.png");
    	BlackBalance blackBal = new BlackBalance(blackimg);
    	CvScalar black = blackBal.getHsvValues();
    //	System.out.print("BLACK:  "); System.out.println(black);
    	
		ColorChart chart = new ColorChart(colorchartsourceRGB, black);
		chart.findCalibColors();
		
		IplImage colorImage = cvLoadImage("test_images/trialcount_img.png");
		IplImage depthImage = cvLoadImage("test_images/trialcount_depth.png");
		ArrayList<Integer> depthPickerData = new ArrayList<Integer>();
		depthPickerData.add(49); depthPickerData.add(319);
		depthPickerData.add(102); depthPickerData.add(121);

		ImageRectifier rectifyImage = new ImageRectifier(colorImage, depthImage, depthPickerData);
		IplImage trialTable = rectifyImage.drawTableLines();
		cvShowImage("depth", trialTable);
		System.out.println(rectifyImage.getDepthData());
		
	//	FindCorners corners = new FindCorners(sourceImage);
	//	corners.findObject();
		
		// to rotate image:
	//	IplImage trialGray = cvCreateImage(cvGetSize(sourceImage), IPL_DEPTH_8U, 1);
	//	cvCvtColor(sourceImage, trialGray, CV_BGR2GRAY);
	//	IplImage trial = blobFinder.SkewGrayImage(trialGray, Math.PI/4);
	//	cvShowImage("skew", trial);
		
		cvWaitKey(0);
	}
}

