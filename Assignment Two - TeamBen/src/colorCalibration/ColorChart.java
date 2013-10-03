package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
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
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;

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
	
	private IplImage silver = null;
	private IplImage gold = null;
	
	private IplImage chartImage;
	
	public CvScalar silverBGR;
	public CvScalar silverHSV;
	public CvScalar silverYCrCb;
	
	public CvScalar goldBGR;
	public CvScalar goldHSV;
	public CvScalar goldYCrCb;
	
	private CvScalar BLACK;
	
	private IplImage sourceImage;
	
	public ColorChart(IplImage sourceImage, CvScalar BLACK) {
		this.sourceImage = sourceImage.clone();
		this.BLACK = BLACK;
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
	
	public String getColorData() {
		String s = "";
		s += "Gold RGB :  "+(int)goldBGR.getVal(2)+", "+(int)goldBGR.getVal(1)+", "+(int)goldBGR.getVal(0)+"\n";
		s += "Gold HSV :  "+(int)goldHSV.getVal(0)+", "+(int)goldHSV.getVal(1)+", "+(int)goldHSV.getVal(2)+"\n";
		s += "Gold YCrCb :  "+(int)goldYCrCb.getVal(0)+", "+(int)goldYCrCb.getVal(1)+", "+(int)goldYCrCb.getVal(2)+"\n";
		s += "\n";
		s += "Silver RGB :  "+(int)silverBGR.getVal(2)+", "+(int)silverBGR.getVal(1)+", "+(int)silverBGR.getVal(0)+"\n";
		s += "Silver HSV :  "+(int)silverHSV.getVal(0)+", "+(int)silverHSV.getVal(1)+", "+(int)silverHSV.getVal(2)+"\n";
		s += "Silver YCrCb :  "+(int)silverYCrCb.getVal(0)+", "+(int)silverYCrCb.getVal(1)+", "+(int)silverYCrCb.getVal(2)+"\n";
		return s;
	}
	
	public boolean findCalibColors() {
		//cvShowImage("source", sourceImage);
		BlobFinder blobFinder = new BlobFinder(sourceImage);
		chartImage = getChartSubImage(blobFinder);
		if (chartImage == null) {
			return false;
		}
	//	cvShowImage("sub image", chartImage);
		
		ArrayList<CvScalar> goldData = getGoldData(chartImage, blobFinder);
		if (goldData == null) {
			return false;
		}
		
		goldBGR = goldData.get(0);
		goldHSV = goldData.get(1);
		goldYCrCb = goldData.get(2);
		
		ArrayList<CvScalar> silverData = getSilverData(chartImage, blobFinder);
		if (silverData == null) {
			return false;
		}
		
		silverBGR = silverData.get(0);
		silverHSV = silverData.get(1);
		silverYCrCb = silverData.get(2);
		
		return true;
	}
	
	public IplImage getChartSubImage(BlobFinder blobFinder) {
		// TURN OFF DRAW ON IMAGE BEFORE FURTHER PROCESSING!!
		
		// set to find black edges of chart
		double blackV = BLACK.getVal(2);
		IplImage chartImage = blobFinder.findBlobs(sourceImage, cvScalar(0, 0, 0, 0),
															cvScalar(255, 255, 50+blackV, 0), 6000);
		
		// WATCH OUT FOR POSSIBLE IMAGE FLIP ON CAPTURE!
		
		ArrayList<Integer> blobData = blobFinder.getData();
		if (blobData.isEmpty()) {
			return null;
		}
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
		IplImage clone = chartImage.clone();
		cvResetImageROI(sourceImage);
		return clone;
	}
	
	public ArrayList<CvScalar> getGoldData(IplImage chartImage, BlobFinder blobFinder) {
		//ideal gold: 27 203 224
		//ideal black: any any 0
		
		// VERY LEFTSIDE GOLD IMAGE! PIC IS REVERSED IN CAPTURE!
		
	//	IplImage gold = blobFinder.findBlobs(chartImage.clone(), cvScalar(0, 110, 180, 0), cvScalar(40, 170, 255, 0), 2000);
		gold = blobFinder.findBlobs(chartImage.clone(),
				cvScalar(10, 100, 100, 0),
				cvScalar(44, 255, 255, 0), 2000);
		// 25 100 150
		//cvShowImage("gold", gold);
		
		ArrayList<Integer> blobCent = blobFinder.getCentres();
		
		int deltax = chartImage.width()/6;
		for (int i=0; i < blobCent.size(); i+=2) {
			int x = blobCent.get(i);
			if (x > deltax) {
				blobCent.remove(i);
				blobCent.remove(i);
				i -= 2;
			}
		}

		if (blobCent.size() < 2) {
			return null;
		}
		//System.out.println(blobCent);
		
		// pos 0,1 is centre of sub image, pos 2,3 should be leftmost orange
		//ArrayList<CvScalar> goldData = getPixelHSV(chartImage, blobCent.get(2), blobCent.get(3));
		ArrayList<CvScalar> goldData = getPixelHSV(chartImage, blobCent.get(0), blobCent.get(1));
		
		CvPoint cp = cvPoint(blobCent.get(0),blobCent.get(1));
        cvCircle(gold, cp, 2, CvScalar.RED, 10, CV_AA, 0);
        
		return goldData;
	}
	
	public ArrayList<CvScalar> getSilverData(IplImage chartImage, BlobFinder blobFinder) {
		//ideal silver 0 0 160
		
		// 3 FROM THE RIGHT! IMAGE REVERSED IN CAPTURE!
		
		//IplImage silver = blobFinder.findBlobs(chartImage.clone(), cvScalar(80, 100, 160, 0), cvScalar(255, 180, 190, 0), 2000);
		silver = blobFinder.findBlobs(chartImage.clone(),
				cvScalar(100, 120, 60, 0),
				cvScalar(200, 255, 200, 0), 2000);
		// 120 30 130
		//cvShowImage("silver", silver);
		
		ArrayList<Integer> blobCent = blobFinder.getCentres();
		
		int deltay = chartImage.height()/4;
		for (int i=0; i < blobCent.size(); i+=2) {
			int y = blobCent.get(i+1);
			if (y < 2*deltay) {
				blobCent.remove(i);
				blobCent.remove(i);
				i -= 2;
			}
		}

		if (blobCent.size() < 2) {
			return null;
		}
		//System.out.println(blobCent);
		
		// pos 0,1 is centre of sub image, pos 2,3 should be leftmost silver
		//ArrayList<CvScalar> silverData = getPixelHSV(chartImage, blobCent.get(2), blobCent.get(3));
		ArrayList<CvScalar> silverData = getPixelHSV(chartImage, blobCent.get(0), blobCent.get(1));
		
		CvPoint cp = cvPoint(blobCent.get(0),blobCent.get(1));
        cvCircle(silver, cp, 2, CvScalar.RED, 10, CV_AA, 0);
        
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
		
		//IplImage colorImg = cvCreateImage(cvGetSize(rgbImage),8,3);
		//cvSet(colorImg, CV_RGB(rgbData.val(2),rgbData.val(1),rgbData.val(0)));
		//cvShowImage("Color fill", colorImg);
		
		ArrayList<CvScalar> values = new ArrayList<CvScalar>();
		values.add(rgbData); values.add(hsvData); values.add(yCrCbData);
		return values;
	}
	
	public IplImage getSilverImg() {
		if (silver == null) {
			return null;
		}
		return silver.clone();
	}
	
	public IplImage getGoldImg() {
		if (gold == null) {
			return null;
		}
		return gold.clone();
	}
	
	public IplImage getSubImage() {
		if (chartImage == null) {
			return null;
		}
		return chartImage.clone();
	}
}
