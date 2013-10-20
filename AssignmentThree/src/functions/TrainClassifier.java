package functions;

import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.NORM_L2;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.io.File;
import java.util.ArrayList;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_features2d.BFMatcher;
import com.googlecode.javacv.cpp.opencv_features2d.DMatch;
import com.googlecode.javacv.cpp.opencv_features2d.FREAK;
import com.googlecode.javacv.cpp.opencv_features2d.FastFeatureDetector;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class TrainClassifier {
    
    static BFMatcher matcher = new BFMatcher(NORM_L2, true);
    CvMat baseDescriptors;
    
    static MatVector mmat;
    
    public TrainClassifier() {
    	
    }
    
    public void init(int number) {
    	mmat = new MatVector(number);
    }
	
	public static void addTrainImage(IplImage trainImage, int pos) {
		KeyPoint keyPoints = new KeyPoint();
		CvMat newDescriptors = new CvMat(null);
	    FastFeatureDetector ffd = new FastFeatureDetector(30, true);
	    ffd.detect(trainImage, keyPoints, null);
	    FREAK extractor = new FREAK();
	    extractor.compute(trainImage, keyPoints, newDescriptors);
	    mmat.put(pos, newDescriptors);
	}
	
	public static void train() {
		matcher.add(mmat);
		matcher.position(0);
		matcher.train();
		matcher.position(0);
	}
	
	public MatVector getTrainingData() {
		return mmat;
	}
	
	public BFMatcher getMatcher() {
		return matcher;
	}
	
	public void trainFromFolder(String location) {
		
		File[] files = new File(location).listFiles();
		
		ArrayList<IplImage> imageList = new ArrayList<IplImage>();
		for (File file : files) {
			IplImage trainImage = cvLoadImage(file.toString());
			imageList.add(trainImage.clone());
		}
		
		this.init(imageList.size());
		
		for (int i=0; i < imageList.size(); i++) {
			IplImage trainImage = imageList.get(i);
			this.addTrainImage(trainImage, i);
		}
		
		this.train();
	}
	
	public void matchImage(IplImage matchImage) {
		KeyPoint keyPoints = new KeyPoint();
		CvMat newDescriptors = new CvMat(null);
	    FastFeatureDetector ffd = new FastFeatureDetector(30, true);
	    ffd.detect(matchImage, keyPoints, null);
	    FREAK extractor = new FREAK();
	    extractor.compute(matchImage, keyPoints, newDescriptors);
	    
	    DMatch matches = new DMatch();
	    
	    matcher.match(newDescriptors, matches, null);
	    System.out.println(matches.capacity());
	}
	
	public static void main(String[] args) {
		
		TrainClassifier trainer = new TrainClassifier();
		trainer.trainFromFolder("training_images/5");
		
		String baseString = "training_images/5/01.png";
		IplImage matchImage = cvLoadImage(baseString);
		
		trainer.matchImage(matchImage);
		
	}
}
