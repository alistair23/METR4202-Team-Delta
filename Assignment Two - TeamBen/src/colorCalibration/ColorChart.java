package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.ArrayList;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import functions.*;

public class ColorChart {

	// Orange Yellow (#e0a32e - chart #12)
	// Neutral 6.5 (#a0a0a0 - chart #21)
	
	private IplImage sourceImage;
	
	public ColorChart(IplImage sourceImage) {
		this.sourceImage = sourceImage.clone();
	}
	
	public void doStuff() {
		EdgesAndLines edgeTool = new EdgesAndLines(sourceImage);
	//	IplImage edges = edgeTool.getEdges();
	//	cvShowImage("Edges", edges);  
	//	cvWaitKey(0);
		
	//	IplImage lines = edgeTool.getLines("probabilistic");
	//	cvShowImage("Lines", lines);  
	//	cvWaitKey(0);
		
		BlobFinder blobFinder = new BlobFinder(sourceImage);
		IplImage blobs = blobFinder.find();
		cvShowImage("Blobs", blobs);  
		cvWaitKey(0);
	}
	
	public void findCalibColors() {
		BlobFinder blobFinder = new BlobFinder(sourceImage);
		blobFinder.find();
		ArrayList<Integer> centrePoints = blobFinder.getCentres();
		
		int right = 0;
		int bottom = 0;
		for (int i=0; i < centrePoints.size(); i+=2) {
			int x = centrePoints.get(i);
			int y = centrePoints.get(i+1);
			if (x > right) {
				right = x;
			}
			if (y > bottom) {
				bottom = y;
			}
		}
		
		ArrayList<Integer> rightColumn = new ArrayList<Integer>();
		ArrayList<Integer> bottomRow = new ArrayList<Integer>();
		for (int i=0; i < centrePoints.size()-1; i+=2) {
			int x = centrePoints.get(i);
			int y = centrePoints.get(i+1);
			if (x > right-50) {
				System.out.print(x); System.out.print(", "); System.out.println(y);
				rightColumn.add(x); rightColumn.add(y);
				CvPoint cp = cvPoint(x, y);
				cvCircle(sourceImage, cp, 6, CvScalar.WHITE, 1, CV_AA, 0);
			}
			
			if (y > bottom-50) {
				//System.out.print(x); System.out.print(", "); System.out.println(y);
				bottomRow.add(x); bottomRow.add(y);
				CvPoint cp = cvPoint(x, y);
				cvCircle(sourceImage, cp, 6, CvScalar.GRAY, 1, CV_AA, 0);
			}
		}
		
		int goldX = rightColumn.get(2);
		int goldY = rightColumn.get(3);
		//System.out.print(goldX); System.out.print(", "); System.out.println(goldY);
		CvPoint cp = cvPoint(goldX, goldY);
		cvCircle(sourceImage, cp, 20, CvScalar.GREEN, 1, CV_AA, 0);
		
		int silverX = bottomRow.get(4);
		int silverY = bottomRow.get(5);
		//System.out.print(silverX); System.out.print(", "); System.out.println(silverY);
		cp = cvPoint(silverX, silverY);
		cvCircle(sourceImage, cp, 20, CvScalar.GREEN, 1, CV_AA, 0);
		
		//ArrayList<CvScalar> goldData = getPixelHSV(sourceImage, goldX, goldY);
		ArrayList<CvScalar> silverData = getPixelHSV(sourceImage, silverX, silverY);
		
		//System.out.print("Gold [R,G,B] : "); System.out.println(goldData.get(0));
		//System.out.print("Gold [H,S,V] : "); System.out.println(goldData.get(1));
		System.out.print("Silver [R,G,B] : "); System.out.println(silverData.get(0));
		System.out.print("Silver [H,S,V] : "); System.out.println(silverData.get(1));
		
		cvShowImage("Color Calib.", sourceImage);  
		cvWaitKey(0);
	}
	
	private static ArrayList<CvScalar> getPixelHSV(IplImage rgbImage, int x, int y) {
		IplImage hsvImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, hsvImage, CV_RGB2HSV);
		
		//y = rgbImage.height()-y;
		CvScalar hsvData = cvGet2D(hsvImage, y, x);
		CvScalar rgbData = cvGet2D(rgbImage, y, x);
		
		IplImage colorImg = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvSet(colorImg, CV_RGB(rgbData.val(0),rgbData.val(1),rgbData.val(2)));
		cvShowImage("Color fill", colorImg);
		
		ArrayList<CvScalar> values = new ArrayList<CvScalar>();
		values.add(rgbData); values.add(hsvData);
		return values;
	}
}
