package junk;

import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_features2d;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC1;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;
import static com.googlecode.javacv.cpp.opencv_nonfree.*;

public class Sifter {

	String library = "/test_images/SIFT_axis/";
	
	
	public static void main(String[] args) {
		initModule_features2d();	
		initModule_nonfree();
		IplImage img = cvLoadImage("test_images/colorchart.png");
		SIFT(img);
	}
	
	
	//Run SIFT on the training image for keypoints, then on img for keypoints then match the keypoints.
	public static IplImage SIFT(IplImage i){
		IplImage ig = IplImage.create(i.cvSize(),IPL_DEPTH_8U,1);
		cvCvtColor(i, ig, CV_BGR2GRAY);
		  CvMat input = ig.asCvMat(); //Load as grayscale
		  //  System.out.print(input);
		  CvMat mask = input.clone() ;//= CvMat.create(ig.width(),ig.height(), CV_8UC1, 1);  // type of mask is CV_8U
		  
		  SIFT detector = new SIFT();
		    
		    System.out.println(detector.isNull());
		    KeyPoint keypoints = new KeyPoint(100);
		    
		    System.out.println( mask.empty());
		    System.out.println( mask.type() == CV_8UC1);
		    System.out.println( mask.size());
		    System.out.println( input.size());
		    System.out.println( mask.empty() || (mask.type() == CV_8UC1 && mask.size() == input.size()));
		    detector.detect(input, mask, keypoints);
		    
		    // Add results to image and save.

		    CvMat output = new CvMat();
		    drawKeypoints((CvArr)input, keypoints, output,new CvScalar(112, 100, 100,0),1);
		    
		    System.out.print(output);
		    //imwrite("sift_result.jpg", output);
		    return output.asIplImage();
	}
	

	
	
	
/**
	void cameraPoseFromHomography(CvMat H, CvMat pose)
	{
	    pose = CvMat.create(3, 4, CV_32FC1);      // 3x4 matrix, the camera pose
	    float norm1 = (float)norm(H.cols(0));  
	    float norm2 = (float)norm(H.cols(1));  
	    float tnorm = (norm1 + norm2) / 2.0f; // Normalization value

	    CvMat p1 = H.cols(0);       // Pointer to first column of H
	    CvMat p2 = pose.cols(0);    // Pointer to first column of pose (empty)

	    cv.normalize(p1, p2);   // Normalize the rotation, and copies the column to pose

	    p1 = H.cols(1);           // Pointer to second column of H
	    p2 = pose.cols(1);        // Pointer to second column of pose (empty)

	    cv.normalize(p1, p2);   // Normalize the rotation and copies the column to pose

	    p1 = pose.cols(0);
	    p2 = pose.cols(1);

	    CvMat p3 = p1.cross(p2);   // Computes the cross-product of p1 and p2
	    CvMat c2 = pose.cols(2);    // Pointer to third column of pose
	    p3.copyTo(c2);       // Third column is the crossproduct of columns one and two

	    pose.cols(3) = H.col(2) / tnorm;  //vector t [R|t] is the last column of pose
	}
	**/
}
