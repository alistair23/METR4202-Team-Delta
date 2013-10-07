package junk;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import cameraCalibration.CameraCalibration4;
import capture.KinectReader;

public class CalibrationTest {

	public static void main(String[] args) {
		
  		KinectReader kr = new KinectReader();
		
		kr.Start();
		
		IplImage[] img = new IplImage[10];
		
		for(int i = 0; i< img.length;i++){
			img[i] = kr.getColorFrame();
		}
		
		
		CameraCalibration4 cc = new CameraCalibration4(new CvSize(6,4), 1, new CvSize(640,480));
		
		
		System.out.println(cc.addChessboardPoints());
		
		System.out.println(cc.calibrate());
		
		IplImage image = cvLoadImage("test_images/chessboard2.jpg");
		
		IplImage undistortedImage = cvLoadImage("test_images/chessboard2.jpg");
		
		cc.remap(image, undistortedImage);
	}
}
