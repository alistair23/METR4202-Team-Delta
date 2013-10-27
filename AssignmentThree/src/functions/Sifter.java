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
	private int threshold = 0;
	
	private IplImage baseImage;
	
	final KeyPoint keyPoints = new KeyPoint();
    final KeyPoint keyPoints2 = new KeyPoint();
    
    int averageX;
	int averageY;
    
    DMatch matches = new DMatch();
	
	public Sifter(IplImage baseImage) {
		this.baseImage = baseImage;
		sift(baseImage);
		this.threshold = (int) ((1.0*getMatchCount())/2);
	}
	
	public void sift(IplImage siftImage) {
		//String smallUrl = "training_images/20/01.jpg";
	    //String largeUrl = "training_images/20/02.jpg";
	    //IplImage image = cvLoadImage(largeUrl,CV_LOAD_IMAGE_UNCHANGED );
	    //IplImage image2 = cvLoadImage(smallUrl,CV_LOAD_IMAGE_UNCHANGED ); 
		
	    CvMat descriptorsA = new CvMat(null);
	    CvMat descriptorsB = new CvMat(null);
	    
	    //final KeyPoint keyPoints = new KeyPoint();
	    //final KeyPoint keyPoints2 = new KeyPoint();
	    
	    //SIFT sift = new SIFT();
	    //sift.detect(baseImage, null, keyPoints);
	    //sift.detect(siftImage, null, keyPoints2);
	    
	    FastFeatureDetector ffd = new FastFeatureDetector(30, true);
	    ffd.detect(baseImage, keyPoints, null);
	    ffd.detect(siftImage, keyPoints2, null);
	    
	    //IplImage featureImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), 3);
	    //drawKeypoints(siftImage, keyPoints2, featureImage, CvScalar.WHITE, DrawMatchesFlags.DRAW_RICH_KEYPOINTS);
	    //cvShowImage("SIFT Features", featureImage);
	    //cvWaitKey(0);

	    //BRISK extractor = new  BRISK();
	    //BriefDescriptorExtractor extractor = new BriefDescriptorExtractor();
	    FREAK extractor = new FREAK();
	    
	    extractor.compute(siftImage, keyPoints2, descriptorsA);
	    extractor.compute(baseImage, keyPoints, descriptorsB);
	    
	    //FlannBasedMatcher matcher = new FlannBasedMatcher();
	    //DescriptorMatcher matcher = new DescriptorMatcher();
	    BFMatcher matcher = new BFMatcher(NORM_L2, true);
	    
	    if (!(descriptorsA.isNull() || descriptorsB.isNull())) {
	    	matcher.match(descriptorsA, descriptorsB, matches, null);
	    	
	    	matchCount = matches.capacity();
	    
		    //IplImage matchImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), baseImage.nChannels());
		    //drawKeypoints(siftImage, keyPoints2, matchImage, CvScalar.YELLOW, DrawMatchesFlags.DEFAULT);
		    //cvShowImage("Matches", matchImage);
		    //cvWaitKey(0);
	    	
	    	//System.out.println("Distance: "+matches.distance());
	    	float sumx = 0; float sumy = 0; int count = 0;
	    	keyPoints2.position(0);
	    	for (int k=0; k < keyPoints2.capacity(); k++) {
	    		//CvPoint2D32f thisPoint = keyPoints2.position(k).pt();
	    		CvPoint2D32f thisPoint = keyPoints2.position(matches.position(0).queryIdx()).pt();
	    		sumx += thisPoint.x();
	    		sumy += thisPoint.y();
	    		count++;
	    	}
	    	keyPoints2.position(0);
	    	averageX = (int)sumx/count;
	    	averageY = (int)sumy/count;
	    	
	    	
	    	
	    	
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
	
	public IplImage drawMatchesOnImage(IplImage toDrawImage) {
		System.out.println(keyPoints.size());
		System.out.println(keyPoints2.size());
		System.out.println(matches.capacity());
		
		if (!matches.isNull() && !keyPoints.isNull() && !keyPoints2.isNull()) {
			IplImage matchImage = IplImage.create(cvGetSize(baseImage), baseImage.depth(), baseImage.nChannels());
		    
		    drawMatches(baseImage, keyPoints, toDrawImage, keyPoints2, matches, matchImage,
		    		CvScalar.BLUE, CvScalar.RED, null, DrawMatchesFlags.DEFAULT);
		    
		    CvPoint pt1 = cvPoint(averageX-20,averageY-20); CvPoint pt2 = cvPoint(averageX+20,averageY+20);
		    cvLine(matchImage, pt1, pt2, CvScalar.RED, 3, 4, 0);
		    CvPoint pt3 = cvPoint(averageX-20,averageY+20); CvPoint pt4 = cvPoint(averageX+20,averageY-20);
		    cvLine(matchImage, pt3, pt4, CvScalar.RED, 3, 4, 0);
		    
		    return matchImage;
		}
		return null;
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
	
	public Boolean isMatch() {
		if (matchCount > threshold) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		
		String baseString = "training_images/20/01.png";
	    String compareString = "training_images/20/02.png";
	    IplImage baseImage = cvLoadImage(baseString);
	    IplImage siftImage = cvLoadImage(compareString);
		
		Sifter sifter = new Sifter(baseImage);
		sifter.sift(siftImage);
		System.out.println("Match Count: "+sifter.getMatchCount());
	}
}
