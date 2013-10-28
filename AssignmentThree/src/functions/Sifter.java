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
    
    int averageX;
	int averageY;
    
    DMatch matches = new DMatch();
    ArrayList<CvPoint2D32f> goodPoints = new ArrayList<CvPoint2D32f>();
    ArrayList<CvPoint2D32f> goodPointsBaseImage = new ArrayList<CvPoint2D32f>();
	
	public Sifter(IplImage baseImage) {
		this.baseImage = baseImage;
	}
	
	public void sift(IplImage siftImage) {
		
		// RE INIT EVERYTHING!!!!
		matches = new DMatch();
		goodPoints = new ArrayList<CvPoint2D32f>();
		goodPointsBaseImage = new ArrayList<CvPoint2D32f>();
		keyPoints = new KeyPoint();
	    keyPoints2 = new KeyPoint();
		
		//String smallUrl = "training_images/20/01.jpg";
	    //String largeUrl = "training_images/20/02.jpg";
	    //IplImage image = cvLoadImage(largeUrl,CV_LOAD_IMAGE_UNCHANGED );
	    //IplImage image2 = cvLoadImage(smallUrl,CV_LOAD_IMAGE_UNCHANGED ); 
		
	    CvMat descriptorsA = new CvMat(null);
	    CvMat descriptorsB = new CvMat(null);
	    
	    //final KeyPoint keyPoints = new KeyPoint();
	    //final KeyPoint keyPoints2 = new KeyPoint();
	    
	    SIFT sift = new SIFT();
	    sift.detect(baseImage, null, keyPoints);
	    sift.detect(siftImage, null, keyPoints2);
	    
	    //FastFeatureDetector ffd = new FastFeatureDetector(30, false);
	    //ffd.detect(baseImage, keyPoints, null);
	    //ffd.detect(siftImage, keyPoints2, null);
	    
	    //IplImage featureImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), 3);
	    //drawKeypoints(siftImage, keyPoints2, featureImage, CvScalar.WHITE, DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
	    //cvShowImage("SIFT Features", featureImage);
	    //cvWaitKey(0);

	    //BRISK extractor = new  BRISK();
	    //BriefDescriptorExtractor extractor = new BriefDescriptorExtractor();
	    FREAK extractor = new FREAK();
	    
	    //extractor.compute(siftImage, descriptorsA, keyPoints2);
	    //extractor.compute(baseImage, descriptorsB, keyPoints);
	    extractor.compute(siftImage, keyPoints2, descriptorsA);
	    extractor.compute(baseImage, keyPoints, descriptorsB);
	    
	    //FlannBasedMatcher matcher = new FlannBasedMatcher();
	    //DescriptorMatcher matcher = new DescriptorMatcher();
	    BFMatcher matcher = new BFMatcher(NORM_L1, true);
	    
	    if (!(descriptorsA.isNull() || descriptorsB.isNull())) {
	    	
	    	matcher.match(descriptorsA, descriptorsB, matches, null);
	    	matchCount = matches.capacity();
	    
		    //IplImage matchImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), baseImage.nChannels());
		    //drawKeypoints(siftImage, keyPoints2, matchImage, CvScalar.YELLOW, DrawMatchesFlags.DEFAULT);
		    //cvShowImage("Matches", matchImage);
		    //cvWaitKey(0);
	    	
	    	//System.out.println("Distance: "+matches.distance());
	    	
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
	    	
	    	System.out.println("MIN: "+minDist+", MAX: "+maxDist);
	    	
	    	if (minDist == 0) {
	    		minDist = 1;
	    	}
	    	/**
	    	for (int i=0; i < matches.capacity(); i++) {
	    		DMatch thismatch = matches.position(i);
	    		float thisDist = thismatch.distance();
	    		if (thisDist > minDist*3) {
	    			thismatch.deallocate();
	    		}
	    	}
	    	System.out.println("# good matches: "+matches.capacity());
	    	*/
	    	
	    	float sumx = 0; float sumy = 0; int count = 0;
	    	keyPoints2.position(0);
	    	for (int k=0; k < matchCount; k++) {
	    		DMatch thismatch = matches.position(k);
	    		if (thismatch.distance() < minDist*2) {
		    		//CvPoint2D32f thisPoint = keyPoints2.position(k).pt();
		    		CvPoint2D32f thisPoint = keyPoints2.position(matches.position(k).queryIdx()).pt();
		    		CvPoint2D32f onBaseImage = keyPoints.position(matches.position(k).trainIdx()).pt();
		    		goodPoints.add(thisPoint);
		    		goodPointsBaseImage.add(onBaseImage);
		    		sumx += thisPoint.x();
		    		sumy += thisPoint.y();
		    		count++;
	    		}
	    	}
	    	keyPoints2.position(0); matches.position(0);
	    	//averageX = (int)sumx/count;
	    	//averageY = (int)sumy/count;
	    	System.out.println("total points --> "+matches.capacity());
	    	System.out.println("good points --> "+goodPoints.size());
	    	
	    	
	/**
		    matches.queryIdx(0);
		    matches.trainIdx(0);
		    System.out.println("queryIdx: "+matches.queryIdx());
		    System.out.println("trainIdx: "+matches.trainIdx());
		    
			// A--> query		B --> train
		    //CvMat matchedDescriptorsA = new CvMat(descriptorsA.rows());
		    CvMat matchedDescriptorsA = cvCreateMat(descriptorsA.rows(), descriptorsA.rows(), CV_32FC1);
		    CvMat matchedDescriptorsB = new CvMat(null);
		    
		    matchedDescriptorsA.position(0);
		    for (int i=0; i < descriptorsA.rows()-1; i++) {
		    	//if (matches.distance() < 100.0) {
		    	
		    	//}
		    }
		    
		    System.out.println(matchedDescriptorsA);
		    
		    //CvMat mmat = cvCreateMat(3,3,CV_32FC1);
		    //cvGetPerspectiveTransform(keyPoints, keyPoints2, mmat);
		    //opencv_calib3d.cvFindHomography(keyPoints, keyPoints2, mmat);
	*/
	    }
	}
	
	public IplImage drawMatchPoints(IplImage toDrawImage) {
		
		if (goodPoints.size() == 0) {
			return toDrawImage;
		} else {
			
			/**
			IplImage matchImage = toDrawImage.clone();
			for (CvPoint2D32f point : goodPoints) {
				CvPoint center = cvPointFrom32f(point);
				cvCircle(matchImage, center, 2, CvScalar.GREEN, 1, CV_AA, 0);
			}
			*/
			
			IplImage matchImage = IplImage.create(cvSize(toDrawImage.width()*2, toDrawImage.height()), baseImage.depth(), baseImage.nChannels());
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
	
	public IplImage drawMatchesOnImage(IplImage toDrawImage) {
		//System.out.println(keyPoints.size());
		//System.out.println(keyPoints2.size());
		//System.out.println(matches.capacity());
		keyPoints2.position(0); keyPoints.position(0);
		matches.position(0);
		
		//if (!matches.isNull() && matches.sizeof() > 0 && !keyPoints.isNull() && !keyPoints2.isNull() &&
		//		keyPoints.size() > 0 && keyPoints2.size() > 0) {
		if (matches.capacity() > 10 && matches.sizeof() > 0 && !keyPoints.isNull() && !keyPoints2.isNull() &&
				keyPoints.size() > 0 && keyPoints2.size() > 0) {
			
			IplImage matchImage = IplImage.create(cvSize(toDrawImage.width()*2, toDrawImage.height()), baseImage.depth(), baseImage.nChannels());
			//IplImage matchImage = toDrawImage.clone();
			
		    drawMatches(toDrawImage, keyPoints2, baseImage, keyPoints, matches, matchImage,
		    		CvScalar.GREEN, CvScalar.RED, null, 0);
		    
		    //drawMatches(baseImage, keyPoints , toDrawImage, keyPoints2, matches, matchImage,
		    //		CvScalar.BLUE, CvScalar.RED, null, 0);
		    
		    CvPoint pt1 = cvPoint(averageX-20,averageY-20); CvPoint pt2 = cvPoint(averageX+20,averageY+20);
		    cvLine(matchImage, pt1, pt2, CvScalar.RED, 3, 4, 0);
		    CvPoint pt3 = cvPoint(averageX-20,averageY+20); CvPoint pt4 = cvPoint(averageX+20,averageY-20);
		    cvLine(matchImage, pt3, pt4, CvScalar.RED, 3, 4, 0);
		    
		    return matchImage;
		} else {
			return toDrawImage;
		}
		
	}
	
	
	public IplImage drawKeyPointsOnImage(IplImage toDrawImage) {
		IplImage matchImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), baseImage.nChannels());
		
	    drawKeypoints(toDrawImage, keyPoints2, matchImage, CvScalar.YELLOW, DrawMatchesFlags.DEFAULT);
	    
	    CvPoint pt1 = cvPoint(averageX-20,averageY-20); CvPoint pt2 = cvPoint(averageX+20,averageY+20);
	    cvLine(matchImage, pt1, pt2, CvScalar.RED, 3, 4, 0);
	    CvPoint pt3 = cvPoint(averageX-20,averageY+20); CvPoint pt4 = cvPoint(averageX+20,averageY-20);
	    cvLine(matchImage, pt3, pt4, CvScalar.RED, 3, 4, 0);
	    
	    return matchImage;
	}
	
	public Integer getMatchCount() {
		return matchCount;
	}
	
	public Float getDistance() {
		return matches.distance();
	}
	
	public static void main(String[] args) {
		
		String baseString = "training_images/5/01.png";
	    String compareString = "training_images/5/02.png";
	    IplImage baseImage = cvLoadImage(baseString);
	    IplImage siftImage = cvLoadImage(compareString);
		
		Sifter sifter = new Sifter(baseImage);
		sifter.sift(siftImage);
		
		//IplImage keypoints = sifter.drawKeyPointsOnImage(siftImage);
		//IplImage matches = sifter.drawMatchesOnImage(siftImage);
		IplImage matches = sifter.drawMatchPoints(siftImage);
		cvShowImage("matches drawn", matches);  
		cvWaitKey(0);
		
	}
}
