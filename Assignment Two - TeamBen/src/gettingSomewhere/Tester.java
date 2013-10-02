package gettingSomewhere;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;

import java.util.ArrayList;

import colorCalibration.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2YCrCb;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Tester {

	public static void main(String[] args) {
		
		
		IplImage colorchartsourceRGB = cvLoadImage("test_images/colorchart.png");
		//cvShowImage("original", colorchartsourceRGB);
		ColorChart chart = new ColorChart(colorchartsourceRGB);
		chart.findCalibColors();
		
		IplImage sourceImageRGB = cvLoadImage("test_images/trialcount_img.png");
	//	cvShowImage("source image", sourceImageRGB);
		IplImage sourceDepthRGB = cvLoadImage("test_images/trialcount_depth.png");
	//	cvShowImage("source depth", sourceDepthRGB);
		
		IplImage trialTable = drawTableLines(sourceImageRGB, sourceDepthRGB);
		
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
		
		cvShowImage("depth", depthImage);
		
		return null;
	}
	
	private static double getPixelValue(IplImage rgbImage, int x, int y) {
		IplImage hsvImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, hsvImage, CV_RGB2HSV);

		return cvGet2D(hsvImage, y, x).getVal(2);
	}

}

