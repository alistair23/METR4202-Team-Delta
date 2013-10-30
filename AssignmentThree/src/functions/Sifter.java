package functions;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;

import com.googlecode.javacv.cpp.opencv_calib3d;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_features2d.*;
import com.googlecode.javacv.cpp.opencv_nonfree.SIFT;

import java.util.ArrayList;

public class Sifter {
	
	private int matchCount = 0;
	
	private IplImage baseImage;
	
	KeyPoint keyPoints = new KeyPoint();
    KeyPoint keyPoints2 = new KeyPoint();
    
    DMatch matches = new DMatch();
    ArrayList<CvPoint2D32f> goodPoints = new ArrayList<CvPoint2D32f>();
    ArrayList<CvPoint2D32f> goodPointsBaseImage = new ArrayList<CvPoint2D32f>();
    int threshold = 140;
	
	public Sifter(IplImage baseImage, int threshold) {
		this.baseImage = baseImage;
		this.threshold = threshold;
	}
	
	public void sift(IplImage siftImage) {
		
		// INIT EVERYTHING!!!!
		matches = new DMatch();
		goodPoints = new ArrayList<CvPoint2D32f>();
		goodPointsBaseImage = new ArrayList<CvPoint2D32f>();
		keyPoints = new KeyPoint();
	    keyPoints2 = new KeyPoint();
		
	    CvMat descriptorsA = new CvMat(null);
	    CvMat descriptorsB = new CvMat(null);
	    
	    // SIFT(int nfeatures=0, int nOctaveLayers=3, double contrastThreshold=0.04, double edgeThreshold=10, double sigma=1.6)
	    //nfeatures – The number of best features to retain. The features are ranked by their scores (measured in SIFT algorithm as the local contrast)
	    //nOctaveLayers – The number of layers in each octave. 3 is the value used in D. Lowe paper. The number of octaves is computed automatically from the image resolution.
	    //contrastThreshold – The contrast threshold used to filter out weak features in semi-uniform (low-contrast) regions. The larger the threshold, the less features are produced by the detector.
	    //edgeThreshold – The threshold used to filter out edge-like features. Note that the its meaning is different from the contrastThreshold, i.e. the larger the edgeThreshold, the less features are filtered out (more features are retained).
	    //sigma – The sigma of the Gaussian applied to the input image at the octave #0. If your image is captured with a weak camera with soft lenses, you might want to reduce the number.
	    
	//    SIFT sift = new SIFT(5000, 2, 0.05, 1.8, 1.6);
	    SIFT sift = new SIFT(5000, 2, 0.04, 1.5, 1.6);
	    //SIFT sift = new SIFT();
	    
	    sift.detect(baseImage, null, keyPoints);
	    sift.detect(siftImage, null, keyPoints2);
	    
	    DescriptorExtractor extractor = sift.getDescriptorExtractor();
	    
	    extractor.compute(siftImage, keyPoints2, descriptorsA);
	    extractor.compute(baseImage, keyPoints, descriptorsB);
	    
	    BFMatcher matcher = new BFMatcher(NORM_L2, true);
	    
	    if (!(descriptorsA.isNull() || descriptorsB.isNull())) {
	    	
	    	matcher.match(descriptorsA, descriptorsB, matches, null);
	    	matchCount = matches.capacity();
	    	
	    	matches.position(0);
	    	float minDist = 10000; float maxDist = 0;
	    	for (int i=0; i < matchCount; i++) {
	    		DMatch thismatch = matches.position(i);
	    		float thisDist = thismatch.distance();
	    		if (thisDist < minDist) {
	    			minDist = thisDist;
	    		} if (thisDist > maxDist) {
	    			maxDist = thisDist;
	    		}	
	    	}
	    	matches.position(0);
	    	
	//    	System.out.println("MIN: "+minDist+", MAX: "+maxDist);
	    	
	    	if (minDist == 0) {
	    		minDist = 1;
	    	}
	    	
	    	keyPoints2.position(0);
	    	for (int k=0; k < matchCount; k++) {
	    		DMatch thismatch = matches.position(k);
	    		if (thismatch.distance() < minDist*3 && thismatch.distance() < threshold) {
		    		CvPoint2D32f thisPoint = keyPoints2.position(matches.position(k).queryIdx()).pt();
		    		CvPoint2D32f onBaseImage = keyPoints.position(matches.position(k).trainIdx()).pt();
		    		goodPoints.add(thisPoint);
		    		goodPointsBaseImage.add(onBaseImage);
	    		}
	    	}
	    	keyPoints2.position(0); matches.position(0);
	    	
	//    	System.out.println("total points --> "+matches.capacity());
	//    	System.out.println("good points --> "+goodPoints.size());
	    	
	    }
	}
	
	public ArrayList<CvPoint2D32f> getGoodMatchPoints() {
		return (ArrayList<CvPoint2D32f>) goodPoints.clone();
	}
	
	public IplImage drawMatchPoints(IplImage toDrawImage) {
		
		if (goodPoints.size() == 0) {
			return toDrawImage;
		} else {
			
			/**
			IplImage matchImage = toDrawImage.clone();
			for (CvPoint2D32f point : goodPoints) {
				CvPoint center = cvPointFrom32f(point);
				cvCircle(matchImage, center, 10, CvScalar.RED, 3, CV_AA, 0);
			}
			*/
			
			IplImage matchImage = IplImage.create(cvSize(baseImage.width()*2, baseImage.height()), baseImage.depth(), baseImage.nChannels());
			cvSetImageROI(matchImage, cvRect(0, 0, 640, 480));
			cvCopy(baseImage, matchImage);
			cvSetImageROI(matchImage, cvRect(640, 0, 640, 480));
			cvCopy(toDrawImage, matchImage);
			cvSetImageROI(matchImage, cvRect(0, 0, 640*2, 480));
			
			int width = toDrawImage.width();
			
			for (int i=0; i < goodPoints.size(); i++) {
				
				CvPoint2D32f siftImagePoint = goodPoints.get(i);
				CvPoint2D32f baseImagePoint = goodPointsBaseImage.get(i);
				
				CvPoint siftImagecenter = cvPointFrom32f(new CvPoint2D32f(siftImagePoint.x()+width, siftImagePoint.y()));
				CvPoint baseImagecenter = cvPointFrom32f(new CvPoint2D32f(baseImagePoint.x(), baseImagePoint.y()));
				
				cvCircle(matchImage, siftImagecenter, 2, CvScalar.GREEN, 1, CV_AA, 0);
				cvCircle(matchImage, baseImagecenter, 2, CvScalar.GREEN, 1, CV_AA, 0);
				cvLine(matchImage, siftImagecenter, baseImagecenter, CvScalar.RED, 1, CV_AA, 0);
			}
			
			return matchImage;
		}
	}
	
	public IplImage drawAllMatchesOnImage(IplImage toDrawImage) {
		keyPoints2.position(0); keyPoints.position(0); matches.position(0);
		
		if (matches.capacity() > 10 && matches.sizeof() > 0 && !keyPoints.isNull() && !keyPoints2.isNull() &&
				keyPoints.size() > 0 && keyPoints2.size() > 0) {
			
			IplImage matchImage = IplImage.create(cvSize(toDrawImage.width()*2, toDrawImage.height()), baseImage.depth(), baseImage.nChannels());
			
		    drawMatches(toDrawImage, keyPoints2, baseImage, keyPoints, matches, matchImage,
		    		CvScalar.GREEN, CvScalar.RED, null, 0);
		    
		    return matchImage;
		} else {
			return toDrawImage;
		}
	}
	
	
	public IplImage drawKeyPointsOnImage(IplImage toDrawImage) {
		IplImage matchImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), baseImage.nChannels());
	    drawKeypoints(toDrawImage, keyPoints2, matchImage, CvScalar.YELLOW, DrawMatchesFlags.DEFAULT);
	    return matchImage;
	}
	
	public Integer getMatchCount() {
		return matchCount;
	}
	
	public static void main(String[] args) {
		
		String baseString = "training_images/$5_back.png";
	    String compareString = "TOSIFT.png";
	    IplImage baseImage = cvLoadImage(baseString);
	    IplImage siftImage = cvLoadImage(compareString);
		
	    ArrayList<CvPoint2D32f> allGoodPoints = new ArrayList<CvPoint2D32f>();
	    // store data over 5 sift operations
//	    for (int i=0; i < 5; i++) {
			Sifter sifter = new Sifter(baseImage, 190);
			sifter.sift(siftImage);
			allGoodPoints.addAll(sifter.getGoodMatchPoints());
			//System.out.println(allGoodPoints.size());
			
			IplImage matches = sifter.drawMatchPoints(siftImage);
			cvShowImage("matches drawn", matches);  
			cvWaitKey(0);
//	    }
	    
	    // get median of all good points
	    CvPoint2D32f bestPoint = allGoodPoints.get((int)(((double)allGoodPoints.size())/2.0));
	    int x = (int) bestPoint.x(); int y = (int) bestPoint.y();
	    System.out.println("Best --> ("+x+", "+y+")");
	    
	    IplImage pointDrawn = siftImage.clone();
	    int d = 20;
	    CvPoint A = cvPointFrom32f(new CvPoint2D32f(x-d, y-d)); CvPoint B = cvPointFrom32f(new CvPoint2D32f(x+d, y+d));
	    CvPoint C = cvPointFrom32f(new CvPoint2D32f(x-d, y+d)); CvPoint D = cvPointFrom32f(new CvPoint2D32f(x+d, y-d));
	    cvLine(pointDrawn, A, B, CvScalar.RED, 2, CV_AA, 0); cvLine(pointDrawn, C, D, CvScalar.RED, 2, CV_AA, 0);
	    cvShowImage("best point drawn", pointDrawn);  
		cvWaitKey(0);
	}
}
