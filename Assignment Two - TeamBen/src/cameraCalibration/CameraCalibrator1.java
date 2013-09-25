package cameraCalibration;
//Code currently stolen/adapted from http://www.chiefdelphi.com/forums/showthread.php?t=101753
	import static com.googlecode.javacv.cpp.opencv_calib3d.CV_CALIB_CB_FAST_CHECK;
	import static com.googlecode.javacv.cpp.opencv_calib3d.cvCalibrateCamera2;
	import static com.googlecode.javacv.cpp.opencv_calib3d.cvDrawChessboardCorners;
	import static com.googlecode.javacv.cpp.opencv_calib3d.cvFindChessboardCorners;
	import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
	import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
	import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_EPS;
	import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_ITER;
	import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
	import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
	import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
	import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
	import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
	import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
	import static com.googlecode.javacv.cpp.opencv_core.cvReleaseMat;
	import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;
	import static com.googlecode.javacv.cpp.opencv_core.cvSize;
	import static com.googlecode.javacv.cpp.opencv_core.cvTermCriteria;
	import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateCameraCapture;
	import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
	import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
	import static com.googlecode.javacv.cpp.opencv_imgproc.CV_WARP_FILL_OUTLIERS;
	import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
	import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindCornerSubPix;
	import static com.googlecode.javacv.cpp.opencv_imgproc.cvInitUndistortMap;
	import static com.googlecode.javacv.cpp.opencv_imgproc.cvRemap;

	import java.util.Scanner;

	import com.googlecode.javacv.CanvasFrame;
	import com.googlecode.javacv.FrameGrabber;
	import com.googlecode.javacv.OpenCVFrameGrabber;
	import com.googlecode.javacv.cpp.opencv_core.CvMat;
	import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
	import com.googlecode.javacv.cpp.opencv_core.CvSize;
	import com.googlecode.javacv.cpp.opencv_core.IplImage;
	import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

	public class CameraCalibrator1 {
		public static void main(String[] args) throws Exception {
			CameraCalibrator1 cc = new CameraCalibrator1();
			String[] argv = new String[] { " ", "4", "5", "10", "210" };
			// empty, internal_w_corners, internal_h_corners, number_of_samples, frames_between_samples

			// Note: frames_between_samples # 30 fps = 7 seconds to resposition the 'chessboard'
			cc.calibrate(argv);
		}

		// ----------------------------------------------------------------------------
		private void calibrate(String[] argv) throws Exception {
			FrameGrabber grabber = new OpenCVFrameGrabber(
					"http://10.13.18.11/mjpg/video.mjpg?resolution=320x240&req_fps=30&.mjpg");
			grabber.start();

			CvCapture capture = null;// CvCreateCameraCapture( 0 );
			// assert( capture );

			if (argv.length != 5) {
				System.out.println("\nERROR: Wrong number of input parameters"
						+ HELP);
				return;
			}

			board_w = Integer.parseInt(argv[1]);
			board_h = Integer.parseInt(argv[2]);
			n_boards = Integer.parseInt(argv[3]);
			board_dt = Integer.parseInt(argv[4]);

			int board_n = board_w * board_h;
			CvSize board_sz = cvSize(board_w, board_h);
			capture = cvCreateCameraCapture(0);
			if (capture == null) {
				System.out.println("\nCouldn't open the camera\n" + HELP);
				return;
			}

			CanvasFrame calibFrame = new CanvasFrame("Calibration");
			CanvasFrame rawFrame = new CanvasFrame("Raw Video");
			CanvasFrame undistortFrame = new CanvasFrame("Undistorted");
			// ALLOCATE STORAGE
			CvMat image_points = cvCreateMat(n_boards * board_n, 2, CV_32FC1);
			CvMat object_points = cvCreateMat(n_boards * board_n, 3, CV_32FC1);
			CvMat point_counts = cvCreateMat(n_boards, 1, CV_32SC1);
			CvMat intrinsic_matrix = cvCreateMat(3, 3, CV_32FC1);
			CvMat distortion_coeffs = cvCreateMat(4, 1, CV_32FC1);

			CvPoint2D32f corners = new CvPoint2D32f(board_n);
			int[] corner_count = { 0 };
			int successes = 0;
			int step, frame = 0;

			IplImage image = grabber.grab();// cvQueryFrame( capture );
			rawFrame.showImage(image);
			IplImage gray_image = cvCreateImage(cvGetSize(image), 8, 1);// subpixel

			// CAPTURE CORNER VIEWS LOOP UNTIL WE’VE GOT n_boards
			// SUCCESSFUL CAPTURES (ALL CORNERS ON THE BOARD ARE FOUND)
			//
			System.out.println(HELP);
			while (successes < n_boards) {
				// Skip every board_dt frames to allow user to move chessboard
				if ((frame++ % board_dt) == 0) {
					// Find chessboard corners:
					int found = cvFindChessboardCorners(image, board_sz, corners,
							corner_count, CV_CALIB_CB_FAST_CHECK);
					System.out.println(String
							.format("found = %s, corner_count = %s", found,
									corner_count[0]));

					// Get Subpixel accuracy on those corners
					cvCvtColor(image, gray_image, CV_BGR2GRAY);
					cvFindCornerSubPix(
							gray_image,
							corners,
							corner_count[0],
							cvSize(11, 11),
							cvSize(-1, -1),
							cvTermCriteria(CV_TERMCRIT_EPS + CV_TERMCRIT_ITER, 30,
									0.1));

					// Draw it
					cvDrawChessboardCorners(image, board_sz, corners,
							corner_count[0], found);

					// If we got a good board, add it to our data
					if (corner_count[0] == board_n) {
						calibFrame.showImage(image); // show in color if we did
														// collect the image
						step = successes * board_n;
						for (int i = step, j = 0; j < board_n; i++, j++) {

							// CV_MAT_ELEM(*image_points, float,i,0) = corners[j].x;
							image_points.put(i, 0, corners.position(j).x());
							System.out.println("corners.position(" + j + ").x()="
									+ corners.position(j).x()
									+", image="+ image_points.get(i,0));
							// CV_MAT_ELEM(*image_points, float,i,1) = corners[j].y;
							image_points.put(i, 1, corners.position(j).y());
							System.out.println("corners.position(" + j + ").y()="
									+ corners.position(j).y()
									+", image="+ image_points.get(i,1));
							// CV_MAT_ELEM(*object_points,float,i,0) = j/board_w;
							object_points.put(i, 0, j / board_w);
							System.out.println("" + j + " / board_w="
									+ (j / board_w)
									+", object_points="+ object_points.get(i, 0));
							// CV_MAT_ELEM(*object_points,float,i,1) = j%board_w;
							object_points.put(i, 1, j % board_w);
							System.out.println("" + j + " % board_w="
									+ (j % board_w)
									+", object_points="+ object_points.get(i, 1));
							// CV_MAT_ELEM(*object_points,float,i,2) = 0.0f;
							object_points.put(i, 2, 0.0);
							System.out.println("0.0=" + (0.0)
									+", object_points="+ object_points.get(i, 2));
						}

						// CV_MAT_ELEM(*point_counts, int,successes,0) = board_n;
						point_counts.put(successes, 0, board_n);

						printMatrix("image_points",image_points);
						printMatrix("object_points",object_points);
						printMatrix("point_counts",point_counts);

						successes++;
						System.out
								.println(String
										.format("Collected our %d of %d needed chessboard images\n",
												successes, n_boards));
					} else
						calibFrame.showImage(gray_image); // Show Gray if we didn't
															// collect the image
				} // end skip board_dt between chessboard capture

				// Handle pause/unpause and ESC
				if ((frame % board_dt) == 0) {
					System.out.println("hit spacebar to contiue...");
					Scanner s = new Scanner(System.in);
					String c = s.nextLine();
					if (c.equals("p")) {
						c = "";
						while (!c.equals("p") && !c.equals("27")) {
							c = s.nextLine();
						}
					}
					if (c.equals("27"))
						return; // todo recognize ESC
				}
				image = grabber.grab();// cvQueryFrame( capture ); //Get next image
				rawFrame.showImage(image);
			} // END COLLECTION WHILE LOOP.
			calibFrame.dispose();
			System.out.println("\n\n*** CALLIBRATING THE CAMERA...");
			// ALLOCATE MATRICES ACCORDING TO HOW MANY CHESSBOARDS FOUND
			CvMat object_points2 = cvCreateMat(successes * board_n, 3, CV_32FC1);
			CvMat image_points2 = cvCreateMat(successes * board_n, 2, CV_32FC1);
			CvMat point_counts2 = cvCreateMat(successes, 1, CV_32SC1);
			// TRANSFER THE POINTS INTO THE CORRECT SIZE MATRICES
			for (int i = 0; i < successes * board_n; ++i) {
				// CV_MAT_ELEM( *image_points2, float, i, 0) = CV_MAT_ELEM(
				// *image_points, float, i, 0);
				image_points2.put(i, 0, image_points.get(i, 0));
				// CV_MAT_ELEM( *image_points2, float,i,1) = CV_MAT_ELEM(
				// *image_points, float, i, 1);
				image_points2.put(i, 1, image_points.get(i, 1));
				// CV_MAT_ELEM(*object_points2, float, i, 0) = CV_MAT_ELEM(
				// *object_points, float, i, 0) ;
				object_points2.put(i, 0, object_points.get(i, 0));
				// CV_MAT_ELEM( *object_points2, float, i, 1) =CV_MAT_ELEM(
				// *object_points, float, i, 1) ;
				object_points2.put(i, 1, object_points.get(i, 1));
				// CV_MAT_ELEM( *object_points2, float, i, 2) =CV_MAT_ELEM(
				// *object_points, float, i, 2) ;
				object_points2.put(i, 2, object_points.get(i, 2));
			}
			for (int i = 0; i < successes; ++i) { // These are all the same number
				// CV_MAT_ELEM( *point_counts2, int, i, 0) = CV_MAT_ELEM(
				// *point_counts,
				// int, i, 0);
				point_counts2.put(i, 0, point_counts.get(i, 0));
			}
			cvReleaseMat(object_points);
			cvReleaseMat(image_points);
			cvReleaseMat(point_counts);

			// At this point we have all of the chessboard corners we need.
			// Initialize the intrinsic matrix such that the two focal
			// lengths have a ratio of 1.0
			//
			// CV_MAT_ELEM( *intrinsic_matrix, float, 0, 0 ) = 1.0f;
			intrinsic_matrix.put(0, 0, 1.0);
			// CV_MAT_ELEM( *intrinsic_matrix, float, 1, 1 ) = 1.0f;
			intrinsic_matrix.put(1, 1, 1.0);

			System.out.println("Before Calibrate Camera 2");
			printMatrix("image_points2",image_points2);
			printMatrix("object_points2",object_points2);
			printMatrix("point_counts2",point_counts2);

			printMatrix("initial intrisic matrix",intrinsic_matrix);
			
			// CALIBRATE THE CAMERA!
			cvCalibrateCamera2(object_points2, image_points2, point_counts2,
					cvGetSize(image), intrinsic_matrix, distortion_coeffs, null,
					null, 0 // CV_CALIB_FIX_ASPECT_RATIO
			);

			System.out.println("After Calibrate Camera 2");
			printMatrix("intrisic matrix",intrinsic_matrix);
			printMatrix("distortion coeffs",distortion_coeffs);

			// SAVE THE INTRINSICS AND DISTORTIONS
			System.out
					.println(" *** DONE!\n\nStoring Intrinsics.xml and Distortions.xml files\n\n");
//			cvSave("Intrinsics.xml", intrinsic_matrix);
//			cvSave("Distortion.xml", distortion_coeffs);
	//
//			// EXAMPLE OF LOADING THESE MATRICES BACK IN:
//			CvMat intrinsic = (CvMat) cvLoad("Intrinsics.xml");
//			CvMat distortion = (CvMat) cvLoad("Distortion.xml");

			CvMat intrinsic = intrinsic_matrix;
			CvMat distortion = distortion_coeffs;

			printMatrix("intrisic",intrinsic);
			printMatrix("distortion",distortion);

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

				// Handle pause/unpause and ESC
//				System.out.println("hit spacebar to contiue...");
//				Scanner s = new Scanner(System.in);
//				String c = s.nextLine();
//				if (c.equals("p")) {
//					c = "";
//					while (!c.equals("p") && !c.equals("27")) {
//						c = s.nextLine();
//					}
//				}
//				if (c.equals("27"))
//					break; // todo recognize ESC
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

