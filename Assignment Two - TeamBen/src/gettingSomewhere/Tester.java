package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;

import java.util.ArrayList;

import colorCalibration.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

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
		
		IplImage sourceImageRGB = cvLoadImage("test_images/trialcount_img.png");
		//cvShowImage("source image", sourceImageRGB);
		IplImage sourceDepthRGB = cvLoadImage("test_images/trialcount_depth.png");
	//	cvShowImage("source depth", sourceDepthRGB);
		
//		IplImage trialTable = drawTableLines(sourceImageRGB, sourceDepthRGB);
		
	//	FindCorners corners = new FindCorners(sourceImage);
	//	corners.findObject();
		
		// to rotate image:
	//	IplImage trialGray = cvCreateImage(cvGetSize(sourceImage), IPL_DEPTH_8U, 1);
	//	cvCvtColor(sourceImage, trialGray, CV_BGR2GRAY);
	//	IplImage trial = blobFinder.SkewGrayImage(trialGray, Math.PI/4);
	//	cvShowImage("skew", trial);
		
		cvWaitKey(0);
	}
	
	private static IplImage drawTableLines(IplImage sourceImage, IplImage depthImage) {
		CvSize imgSize = cvGetSize(sourceImage);
		//cvShowImage("source", sourceImage);
		//cvShowImage("depth", depthImage);
		
		int x = imgSize.width()/6;
		double initValue = getPixelValue(depthImage, x, imgSize.height()-10);
		System.out.println(initValue);
		double prevPixelValue = 255.0;
		
		for (int i = imgSize.height()/2; i < imgSize.height()-10; i++) {
			double pixelValue = getPixelValue(depthImage, x, i);
			if ((pixelValue%5 == 0.0) && (pixelValue != prevPixelValue) && (pixelValue != 255.0)) {
				System.out.print(i+" Value: "); System.out.println(pixelValue);
				prevPixelValue = pixelValue;
				CvPoint pt1 = new CvPoint(x-10, i); CvPoint pt2 = new CvPoint(x+10, i);
				cvLine(depthImage, pt1, pt2, CV_RGB(255, 0, 0), 1, CV_AA, 0);
			}
		}
		
		//cvShowImage("depth", depthImage);
		
		return null;
	}
	
	private static double getPixelValue(IplImage rgbImage, int x, int y) {
		IplImage hsvImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, hsvImage, CV_RGB2HSV);

		return cvGet2D(hsvImage, y, x).getVal(2);
	}

}

