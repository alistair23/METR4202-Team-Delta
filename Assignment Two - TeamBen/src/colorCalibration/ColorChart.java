package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.util.ArrayList;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class is used to identify a MacBeth color chart within an image
 * and find the color data corresponding to both:
 * Orange Yellow (#e0a32e - chart #12)
 * Neutral 6.5 (#a0a0a0 - chart #21)
 *
 */

public class ColorChart {
	
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
		
		BlobFinder blobFinder = new BlobFinder(sourceImage);
		chartImage = getChartSubImage(blobFinder);
		if (chartImage == null) {
			return false;
		}
		
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
		// NOTE: WILL DRAW FOUND BLOBS ONTO OUTPUT IMAGE
		
		// set to find black edges of chart
		double blackV = BLACK.getVal(2);
		IplImage chartImage = blobFinder.findBlobs(sourceImage, cvScalar(0, 0, 0, 0),
															cvScalar(255, 255, 50+blackV, 0), 6000);
		
		ArrayList<Integer> blobData = blobFinder.getData();
		if (blobData.isEmpty()) {
			return null;
		}
		int x = blobData.get(0); int y = blobData.get(1);
		int width = blobData.get(2)-blobData.get(0);
		int height = blobData.get(3)-blobData.get(1);
		
		CvRect roi = cvRect(x, y, width, height);
		
		cvSetImageROI(chartImage, roi);
		IplImage clone = chartImage.clone();
		cvResetImageROI(sourceImage);
		return clone;
	}
	
	// threshold out gold then get choose appropriate blob data.
	public ArrayList<CvScalar> getGoldData(IplImage chartImage, BlobFinder blobFinder) {
		//ideal gold: 27 203 224
		
		gold = blobFinder.findBlobs(chartImage.clone(),
				cvScalar(10, 0, 100, 0),
				cvScalar(70, 255, 255, 0), 2000);
		
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

		ArrayList<CvScalar> goldData = getPixelHSV(chartImage, blobCent.get(0), blobCent.get(1));
		CvPoint cp = cvPoint(blobCent.get(0),blobCent.get(1));
        cvCircle(gold, cp, 2, CvScalar.RED, 10, CV_AA, 0);
        
		return goldData;
	}
	
	// threshold out silver then get choose appropriate blob data.
	public ArrayList<CvScalar> getSilverData(IplImage chartImage, BlobFinder blobFinder) {
		//ideal silver 0 0 160
		
		silver = blobFinder.findBlobs(chartImage.clone(),
				cvScalar(100, 120, 100, 0),
				cvScalar(200, 255, 200, 0), 2000);
		
		ArrayList<Integer> blobCent = blobFinder.getCentres();
		
		int deltay = 2*chartImage.height()/4;
		for (int i=0; i < blobCent.size(); i+=2) {
			int y = blobCent.get(i+1);
			if (y < deltay) {
				blobCent.remove(i);
				blobCent.remove(i);
				i -= 2;
			}
		}

		if (blobCent.size() < 2) {
			return null;
		}
		
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
