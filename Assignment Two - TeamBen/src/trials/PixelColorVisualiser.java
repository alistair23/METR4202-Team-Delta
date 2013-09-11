package trials;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
 
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_highgui.*;
 
public class PixelColorVisualiser{
  
	public static int x_co;
	public static int y_co; 
	
	public static void main (String[] args){
		
		final IplImage src = cvLoadImage("img1.png");//Images 'test.png' located under resource folder
		cvNamedWindow("Image",CV_WINDOW_AUTOSIZE);
		final IplImage hsv = cvCreateImage(cvGetSize(src), 8, 3);
	    cvCvtColor(src, hsv, CV_BGR2HSV);
	    
	    CvMouseCallback on_mouse = new CvMouseCallback() {
            @Override
            public void call(int event, int x, int y, int flags, 
            		com.googlecode.javacpp.Pointer param) {
            	if (event == CV_EVENT_MOUSEMOVE){
            		x_co = x;
            		y_co = y;
            	}
            	CvScalar s=cvGet2D(hsv,y_co,x_co);                
                System.out.println( "H:"+ s.val(0) + " S:" + s.val(1) + " V:" + s.val(2));//Print values
            }
        };
        
        cvSetMouseCallback("Image", on_mouse, null);        
	    cvShowImage("Image", src);
	    cvWaitKey(0);
	}	
}