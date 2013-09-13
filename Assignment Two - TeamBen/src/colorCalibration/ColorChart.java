package colorCalibration;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPointFrom32f;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.ArrayList;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import functions.*;

public class ColorChart {

	// Orange Yellow (#e0a32e - chart #12)
	// Neutral 6.5 (#a0a0a0 - chart #21)
	
	private IplImage sourceImage;
	
	public ColorChart(IplImage sourceImage) {
		this.sourceImage = sourceImage;
	}
	
	public void doStuff() {
		EdgesAndLines edgeTool = new EdgesAndLines(sourceImage);
		IplImage edges = edgeTool.getEdges();
		cvShowImage("Edges", edges);  
		cvWaitKey(0);
	}
}
