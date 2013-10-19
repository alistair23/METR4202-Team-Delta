package functions;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_features2d.BFMatcher;
import com.googlecode.javacv.cpp.opencv_features2d.DMatch;
import com.googlecode.javacv.cpp.opencv_features2d.DescriptorMatcher;
import com.googlecode.javacv.cpp.opencv_features2d.FREAK;
import com.googlecode.javacv.cpp.opencv_features2d.FastFeatureDetector;
import com.googlecode.javacv.cpp.opencv_features2d.FlannBasedMatcher;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;

import java.util.ArrayList;

public class Sifter {
	
	private int matchCount = 0;
	private int threshold = 0;
	
	private IplImage baseImage;
	
	public Sifter(IplImage baseImage) {
		this.baseImage = baseImage;
		sift(baseImage);
		this.threshold = (int) ((2.0*getMatchCount())/3);
	}
	
	public void sift(IplImage siftImage) {
		
		//String smallUrl = "training_images/20/01.jpg";
	    //String largeUrl = "training_images/20/02.jpg";
	    //IplImage image = cvLoadImage(largeUrl,CV_LOAD_IMAGE_UNCHANGED );
	    //IplImage image2 = cvLoadImage(smallUrl,CV_LOAD_IMAGE_UNCHANGED ); 
		
	    CvMat descriptorsA = new CvMat(null);
	    CvMat descriptorsB = new CvMat(null);

	    final FastFeatureDetector ffd = new FastFeatureDetector(40, true);
	    final KeyPoint keyPoints = new KeyPoint();
	    final KeyPoint keyPoints2 = new KeyPoint();

	    ffd.detect(baseImage, keyPoints, null);
	    ffd.detect(siftImage, keyPoints2, null);

//	    System.out.println("keyPoints.size() : "+keyPoints.size());
//	    System.out.println("keyPoints2.size() : "+keyPoints2.size());
	     // BRISK extractor = new  BRISK();
	     //BriefDescriptorExtractor extractor = new BriefDescriptorExtractor();
	    FREAK extractor = new FREAK();
	    
	    extractor.compute(siftImage, keyPoints2, descriptorsA);
	    extractor.compute(baseImage, keyPoints, descriptorsB);
	    
//	    System.out.println("descriptorsA.size() : "+descriptorsA.size());
//	    System.out.println("descriptorsB.size() : "+descriptorsB.size());

	    DMatch dmatch = new DMatch();
	    //FlannBasedMatcher matcher = new FlannBasedMatcher();
	    //DescriptorMatcher matcher = new DescriptorMatcher();
	    BFMatcher matcher = new BFMatcher();
	    
	    if (!(descriptorsA.isNull() || descriptorsB.isNull())) {
	    	matcher.match(descriptorsA, descriptorsB, dmatch, null);
	    	matchCount = dmatch.capacity();
	    }
	    
	    
	    
	}
	
	public Integer getMatchCount() {
		return matchCount;
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
		System.out.println(sifter.getMatchCount());
	}
}
