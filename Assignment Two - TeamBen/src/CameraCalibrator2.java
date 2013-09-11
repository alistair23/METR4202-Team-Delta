
	import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
	import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
	import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
	import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
	import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
	import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
	import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
	import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;
	import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
	import static com.googlecode.javacv.cpp.opencv_imgproc.CV_WARP_FILL_OUTLIERS;
	import static com.googlecode.javacv.cpp.opencv_imgproc.cvInitUndistortMap;
	import static com.googlecode.javacv.cpp.opencv_imgproc.cvRemap;

	import com.googlecode.javacv.CanvasFrame;
	import com.googlecode.javacv.FrameGrabber;
	import com.googlecode.javacv.OpenCVFrameGrabber;
	import com.googlecode.javacv.cpp.opencv_core.CvMat;
	import com.googlecode.javacv.cpp.opencv_core.IplImage;

	public class CameraCalibrator2 {
		public static void main(String[] args) throws Exception {
			CameraCalibrator2 cc = new CameraCalibrator2();
			cc.calibrate(args); // no arguments are required
		}

		// ----------------------------------------------------------------------------
		private void calibrate(String[] argv) throws Exception {
			FrameGrabber grabber = new OpenCVFrameGrabber(
					"http://10.13.18.11/mjpg/video.mjpg?resolution=320x240&req_fps=30&.mjpg");
			grabber.start();

			CanvasFrame undistortFrame = new CanvasFrame("Undistort");
			CanvasFrame rawFrame = new CanvasFrame("Raw Video");

			CvMat intrinsic_matrix = cvCreateMat(3, 3, CV_32FC1);
			CvMat distortion_coeffs = cvCreateMat(4, 1, CV_32FC1);

			// At this point we have all of the chessboard corners we need.
			// Initialize the intrinsic matrix such that the two focal
			// lengths have a ratio of 1.0
			//
			// the CV_MAT_ELEM() macro corresponds to CvMat.put(row,col,val) and CvMat.get(row,col).
			// the put and get methods account for the primitive type lengths using pointer arithmetic.
			// CV_MAT_ELEM( *intrinsic_matrix, float, 0, 0 ) = 1.0f;
			intrinsic_matrix.put(0, 0, 160); // focal length x
			intrinsic_matrix.put(0, 1, 0.0);
			intrinsic_matrix.put(0, 2, 160.0); // center x

			intrinsic_matrix.put(1, 0, 0.0);
			intrinsic_matrix.put(1, 1, 120); // focal length y [= x * h / w]
			intrinsic_matrix.put(1, 2, 120.0); // center y

			intrinsic_matrix.put(2, 0, 0.0);
			intrinsic_matrix.put(2, 1, 0.0);
			intrinsic_matrix.put(2, 2, 1.0); // flat z

			
			double p_factor = 0.00;
			
			distortion_coeffs.put(0, 0, -0.055); // k1 * r^2
			distortion_coeffs.put(1, 0, 0.0); // k2 * r^4
			distortion_coeffs.put(2, 0, -p_factor); // tangential p1
			distortion_coeffs.put(3, 0, p_factor); // tangential p2

			printMatrix("intrisic matrix",intrinsic_matrix);
			printMatrix("distortion coeffs",distortion_coeffs);

			// SAVE THE INTRINSICS AND DISTORTIONS
			System.out
					.println(" *** DONE!\n\nStoring Intrinsics.xml and Distortions.xml files\n\n");
	//todo: print out to xml file
//			cvSave("Intrinsics.xml", intrinsic_matrix);
//			cvSave("Distortion.xml", distortion_coeffs);
	//

	// EXAMPLE OF LOADING THESE MATRICES BACK IN:
//			CvMat intrinsic = (CvMat) cvLoad("Intrinsics.xml");
//			CvMat distortion = (CvMat) cvLoad("Distortion.xml");

			CvMat intrinsic = intrinsic_matrix;
			CvMat distortion = distortion_coeffs;

			printMatrix("intrisic",intrinsic);
			printMatrix("distortion",distortion);

			IplImage image = grabber.grab();// cvQueryFrame( capture );
			rawFrame.showImage(image);

			// Build the undistort map which we will use for all
			// subsequent frames.
			//
			IplImage mapx = cvCreateImage(cvGetSize(image), IPL_DEPTH_32F, 1);
			IplImage mapy = cvCreateImage(cvGetSize(image), IPL_DEPTH_32F, 1);
			cvInitUndistortMap(intrinsic, distortion, mapx, mapy);
			// Just run the camera to the screen, now showing the raw and
			// the undistorted image.
			//
			while (image != null) {
				IplImage t = cvCloneImage(image);
				rawFrame.showImage(image); // Show raw image
				cvRemap(t, image, mapx, mapy, CV_INTER_LINEAR
						| CV_WARP_FILL_OUTLIERS, cvScalarAll(0));
				cvReleaseImage(t);
				undistortFrame.showImage(image); // Show corrected image

				image = grabber.grab();// cvQueryFrame( capture );
			}
		}
		
		private void printMatrix(String label, CvMat matrix) {
			int rows = matrix.rows();
			int cols = matrix.cols();
			
			System.out.println("matrix "+label);
			for (int i=0; i<rows; i++) {
				String rowStr = "(";
				for (int j=0; j<cols; j++) {
					rowStr += matrix.get(i,j);
					if (j<cols-1) rowStr+=",";
				}
				rowStr += ")";
				System.out.println(rowStr);
			}
			
			
		}

		// ----------------------------------------------------------------------------

		// Example 11-1. Reading a chessboard’s width and height, reading and
		// collecting the
		// requested number of views, and calibrating the camera
		//

		public static final String HELP = "\n\n"
				+ " Calling convention:\n"
				+ " ch11_ex11_1  board_w  board_h  number_of_boards  skip_frames\n"
				+ "\n"
				+ "   WHERE:\n"
				+ "     board_w, board_h   -- are the number of corners along the row and columns respectively\n"
				+ "     number_of_boards   -- are the number of chessboard views to collect before calibration\n"
				+ "     skip_frames        -- are the number of frames to skip before trying to collect another\n"
				+ "                           good chessboard.  This allows you time to move the chessboard.  \n"
				+ "                           Move it to many different locations and angles so that calibration \n"
				+ "                           space will be well covered. \n"
				+ "\n" + " Hit ‘p’ to pause/unpause, ESC to quit\n" + "\n";

		//
		/*
		 * *************** License:************************** Oct. 3, 2008 Right to
		 * use this code in any way you want without warrenty, support or any
		 * guarentee of it working.
		 * 
		 * BOOK: It would be nice if you cited it: Learning OpenCV: Computer Vision
		 * with the OpenCV Library by Gary Bradski and Adrian Kaehler Published by
		 * O'Reilly Media, October 3, 2008
		 * 
		 * AVAILABLE AT:
		 * http://www.amazon.com/Learning-OpenCV-Computer-Vision-Library
		 * /dp/0596516134 Or: http://oreilly.com/catalog/9780596516130/ ISBN-10:
		 * 0596516134 or: ISBN-13: 978-0596516130
		 * 
		 * OTHER OPENCV SITES: The source code is on sourceforge at:
		 * http://sourceforge.net/projects/opencvlibrary/ The OpenCV wiki page (As
		 * of Oct 1, 2008 this is down for changing over servers, but should come
		 * back): http://opencvlibrary.sourceforge.net/ An active user group is at:
		 * http://tech.groups.yahoo.com/group/OpenCV/ The minutes of weekly OpenCV
		 * development meetings are at: http://pr.willowgarage.com/wiki/OpenCV
		 * *************************************************
		 */
		//
		int n_boards = 1; // Will be set by input list
		int board_dt = 90; // Wait 90 frames per chessboard view
		int board_w = 8;
		int board_h = 8;
	}

