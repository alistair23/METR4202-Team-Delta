package colorCalibration;

import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.awt.Rectangle;
import java.io.File;


/** Uses the mean shift algorithm to find best matching location of the 'template' in another image.
  *
  * Matching is done using the hue channel of the input image converted to HSV color space.
  * Histogram of a region in the hue channel is used to create a 'template'.
  *
  * The target image, where we want to find a matching region, is also converted to HSV.
  * Histogram of the template is back projected in the hue channel.
  * The mean shift algorithm searches in the back projected image to find best match to the template.
  *
  * Example for section "Using the mean shift algorithm to find an object" in Chapter 4.
  */

class MeanShiftDetector {
	
	private IplImage templateImage;
	
	public MeanShiftDetector(IplImage templateImage) {
		this.templateImage = templateImage;
	}
	
	public void detectInImage(IplImage targetImage) {
	
	    // Compute histogram within the ROI
	    int minSaturation = 65;
	    //CvHistogram templateHueHist = new CvHistogram().getHueHistogram(templateImage, minSaturation);
	    
	    IplImageArray imgArray = new IplImageArray(0);
	    imgArray.put(targetImage);
	    
	    CvHistogram templateHist = cvCreateHist(1, new int[]{255}, CV_HIST_ARRAY, new float[][]{new  float[]{0f, 180f}}, 1);
	    cvCalcHist(imgArray,templateHist, 1, null);
	
	    // Convert to HSV color space
	    IplImage hsvTargetImage = IplImage.create(cvGetSize(targetImage), targetImage.depth(), 3);
	    cvCvtColor(targetImage, hsvTargetImage, CV_BGR2HSV);
	
	    // Identify pixels with low saturation
	    val saturationChannel = ColorHistogram.splitChannels(hsvTargetImage)(1);
	    cvThreshold(saturationChannel, saturationChannel, minSaturation, 255, CV_THRESH_BINARY);
	    show(saturationChannel, "Target saturation mask");
	
	    // Get back-projection of the hue histogram of the 'template'
	    val finder = new ContentFinder();
	    finder.histogram = templateHueHist;
	    val result = finder.find(hsvTargetImage);
	    show(result, "Back-projection.");
	
	    // Eliminate low saturation pixels, to reduce noise abd improve search quality
	    cvAnd(result, saturationChannel, result, null);
	    show(result, "Back-projection with reduced saturation pixels.");
	
	    // Starting position for the search
	    val targetRect = new CvRect();
	    targetRect.x(rect.x);
	    targetRect.y(rect.y);
	    targetRect.width(rect.width);
	    targetRect.height(rect.height);
	
	    // Search termination criteria
	    val termCriteria = new CvTermCriteria();
	    termCriteria.max_iter(10);
	    termCriteria.epsilon(0.01);
	    termCriteria.`type`(CV_TERMCRIT_ITER);
	
	    // Search using mean shift algorithm.
	    val searchResults = new CvConnectedComp();
	    val iterations = cvMeanShift(result, targetRect, termCriteria, searchResults);
	    show(drawOnImage(targetImage, toRectangle(searchResults.rect())), "Output in " + iterations + " iterations.");
	}
	
	private CvHistogram getHueHistogram(IplImage image){
	    if(image==null || image.nChannels()<3) new Exception("Error!");
	    IplImage hsvImage= cvCreateImage(image.cvSize(), image.depth(), 3);
	    cvCvtColor(image, hsvImage, CV_BGR2HSV);
	    // Split the 3 channels into 3 images
	    IplImageArray hsvChannels = splitChannels(hsvImage);
	    //bins and value-range
	    int numberOfBins=255;
	    float minRange= 0f;
	    float maxRange= 180f;
	    // Allocate histogram object
	   int dims = 1;
	   int[]sizes = new int[]{numberOfBins};
	   int histType = CV_HIST_ARRAY;
	   float[] minMax = new  float[]{minRange, maxRange};
	   float[][] ranges = new float[][]{minMax};
	    int uniform = 1;
	    CvHistogram hist = cvCreateHist(dims, sizes, histType, ranges, uniform);
	    // Compute histogram
	    int accumulate = 1;
	    IplImage mask = null;
	    cvCalcHist(hsvChannels.position(0),hist, accumulate, null);
	    return hist;
	}
	
	private IplImageArray splitChannels(IplImage hsvImage) {
	    CvSize size = hsvImage.cvSize();
	    int depth=hsvImage.depth();
	    IplImage channel0 = cvCreateImage(size, depth, 1);
	    IplImage channel1 = cvCreateImage(size, depth, 1);
	    IplImage channel2 = cvCreateImage(size, depth, 1);
	    cvSplit(hsvImage, channel0, channel1, channel2, null);
	    return new IplImageArray(channel0, channel1, channel2);
	}
}