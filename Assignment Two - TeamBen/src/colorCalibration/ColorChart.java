package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import functions.*;

public class ColorChart {

	// Orange Yellow (#e0a32e - chart #12)
	// Neutral 6.5 (#a0a0a0 - chart #21)
	
	public CvScalar GOLDHSVDATA;
	public CvScalar SILVERHSVDATA; 
	
	private IplImage sourceImage;
	
	public ColorChart(IplImage sourceImage) {
		this.sourceImage = sourceImage.clone();
	}
	
	public void doStuff() {
	//	EdgesAndLines edgeTool = new EdgesAndLines(sourceImage);
	//	IplImage edges = edgeTool.getEdges();
	//	cvShowImage("Edges", edges);  
	//	cvWaitKey(0);
		
	//	IplImage lines = edgeTool.getLines("probabilistic");
	//	cvShowImage("Lines", lines);  
	//	cvWaitKey(0);
		
	//	BlobFinder blobFinder = new BlobFinder(sourceImage);
	//	IplImage blobs = blobFinder.getChartSubImage();
	//	cvShowImage("Blobs", blobs);
	//	cvWaitKey(0);
	}
	
	public void findCalibColors() {
		BlobFinder blobFinder = new BlobFinder(sourceImage);
		IplImage chartImage = getChartSubImage(blobFinder);
		
	//	cvShowImage("source", sourceImage);
		
		ArrayList<CvScalar> goldData = getGoldData(chartImage, blobFinder);
		GOLDHSVDATA = goldData.get(1);
		CvScalar rgb = goldData.get(0);
		CvScalar hsv = goldData.get(1);
		CvScalar ycrcb = goldData.get(2);
		
		System.out.print("Gold RGB :  "); System.out.print((int)rgb.getVal(2)+", ");
		System.out.print((int)rgb.getVal(1)+", "); System.out.println((int)rgb.getVal(0));
		System.out.print("Gold HSV :  "); System.out.print((int)hsv.getVal(0)+", ");
		System.out.print((int)hsv.getVal(1)+", "); System.out.println((int)hsv.getVal(2));
		System.out.print("Gold YCrCb :  "); System.out.print((int)ycrcb.getVal(0)+", ");
		System.out.print((int)ycrcb.getVal(1)+", "); System.out.println((int)ycrcb.getVal(2));
		
		System.out.println("");
		
		ArrayList<CvScalar> silverData = getSilverData(chartImage, blobFinder);
		SILVERHSVDATA = silverData.get(1);
		rgb = silverData.get(0);
		hsv = silverData.get(1);
		ycrcb = silverData.get(2);
		
		System.out.print("Silver RGB :  "); System.out.print((int)rgb.getVal(2)+", ");
		System.out.print((int)rgb.getVal(1)+", "); System.out.println((int)rgb.getVal(0));
		System.out.print("Silver HSV :  "); System.out.print((int)hsv.getVal(0)+", ");
		System.out.print((int)hsv.getVal(1)+", "); System.out.println((int)hsv.getVal(2));
		System.out.print("Silver YCrCb :  "); System.out.print((int)ycrcb.getVal(0)+", ");
		System.out.print((int)ycrcb.getVal(1)+", "); System.out.println((int)ycrcb.getVal(2));
		
		cvWaitKey(0);
	}
	
	public IplImage getChartSubImage(BlobFinder blobFinder) {
		// TURN OFF DRAW ON IMAGE BEFORE FURTHER PROCESSING!!
		IplImage chartImage = blobFinder.findBlobs(sourceImage, cvScalar(15, 0, 20, 0), cvScalar(30, 100, 70, 0), 6000);
		ArrayList<Integer> blobData = blobFinder.getData();
		int x = blobData.get(0); int y = blobData.get(1);
		int width = blobData.get(2)-blobData.get(0);
		int height = blobData.get(3)-blobData.get(1);
		
		CvRect roi = cvRect(x, y, width, height);
	//	System.out.println(roi.width());
		
		//IplImage gray = cvCreateImage(cvGetSize(sourceImage), IPL_DEPTH_8U, 1); 
		//cvCloneImage(sourceImage, gray);
		
	//	IplImage cropped = cvCreateImage(cvSize(roi.width(), roi.height()), sourceImage.depth(), 1);
		cvSetImageROI(chartImage, roi);
		//cvCopy(chartImage, cropped);
		//cvResetImageROI(chartImage);
		
		//sourceImage.copyTo(buff);
		
		return chartImage;
	}
	
	public ArrayList<CvScalar> getGoldData(IplImage chartImage, BlobFinder blobFinder) {

		IplImage gold = blobFinder.findBlobs(chartImage.clone(), cvScalar(0, 110, 180, 0), cvScalar(40, 170, 255, 0), 2000);
		//cvShowImage("gold", gold);
		
		ArrayList<Integer> blobCent = blobFinder.getCentres();
		if (blobCent.size() != 4) {
			System.out.println("Bad read! Try again.");
		}
		
		ArrayList<CvScalar> goldData = getPixelHSV(chartImage, blobCent.get(2), blobCent.get(3));
		return goldData;
	}
	
	public ArrayList<CvScalar> getSilverData(IplImage chartImage, BlobFinder blobFinder) {

		IplImage silver = blobFinder.findBlobs(chartImage.clone(), cvScalar(80, 100, 160, 0), cvScalar(255, 180, 190, 0), 2000);
		//cvShowImage("silver", silver);
		
		ArrayList<Integer> blobCent = blobFinder.getCentres();
		if (blobCent.size() != 4) {
			System.out.println("Bad read! Try again.");
		}
		
		ArrayList<CvScalar> silverData = getPixelHSV(chartImage, blobCent.get(2), blobCent.get(3));
		return silverData;
	}
	
	private static ArrayList<CvScalar> getPixelHSV(IplImage rgbImage, int x, int y) {
		IplImage hsvImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, hsvImage, CV_RGB2HSV);
		IplImage yCrCbImage = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvCvtColor(rgbImage, yCrCbImage, CV_RGB2YCrCb);

		CvScalar rgbData = cvGet2D(rgbImage, y, x);
		CvScalar hsvData = cvGet2D(hsvImage, y, x);
		CvScalar yCrCbData = cvGet2D(yCrCbImage, y, x);
		
		IplImage colorImg = cvCreateImage(cvGetSize(rgbImage),8,3);
		cvSet(colorImg, CV_RGB(rgbData.val(2),rgbData.val(1),rgbData.val(0)));
		//cvShowImage("Color fill", colorImg);
		
		ArrayList<CvScalar> values = new ArrayList<CvScalar>();
		values.add(rgbData); values.add(hsvData); values.add(yCrCbData);
		return values;
	}
}
