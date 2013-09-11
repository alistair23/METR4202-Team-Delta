package trials;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
 
public class HoughCircles{
 public static void main(String[] args){
  IplImage src = cvLoadImage("img1.png");
  IplImage gray = cvCreateImage(cvGetSize(src), 8, 1);
   
  cvCvtColor(src, gray, CV_BGR2GRAY);  
  cvSmooth(gray, gray, CV_GAUSSIAN, 3);
   
  CvMemStorage mem = CvMemStorage.create();
   
  CvSeq circles = cvHoughCircles( 
    gray, //Input image
    mem, //Memory Storage
    CV_HOUGH_GRADIENT, //Detection method
    1, //Inverse ratio
    10, //Minimum distance between the centers of the detected circles
    20, //Higher threshold for canny edge detector
    50, //Threshold at the center detection stage
    10, //min radius
    100 //max radius
    );
   
  for(int i = 0; i < circles.total(); i++){
      CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
      CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
      int radius = Math.round(circle.z());      
      cvCircle(src, center, radius, CvScalar.GREEN, 1, CV_AA, 0);    
     }
   
  cvShowImage("Result",src);  
  cvWaitKey(0);
   
 }
}