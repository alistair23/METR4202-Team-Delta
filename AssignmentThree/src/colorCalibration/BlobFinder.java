package colorCalibration;

import java.util.ArrayList;
import com.googlecode.javacv.Blobs;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * Adapted from the javaCV cookbook examples.
 * https://code.google.com/p/javacv/wiki/OpenCV2_Cookbook_Examples
 *
 * This class is used to find 'blobs' of color within a given threshold and pixel size.
 *
 */

public class BlobFinder {
	
	private static IplImage sourceImage;
	private ArrayList<Integer> blobCent;
	private ArrayList<Integer> blobData;
	
	public BlobFinder(IplImage source) {
		BlobFinder.sourceImage = source.clone();
		blobData = new ArrayList<Integer>();
		blobCent = new ArrayList<Integer>();
	}
	
    public IplImage findBlobs(IplImage RawImage, CvScalar minThresh, CvScalar maxThresh, int MinArea) {
    	
    	blobCent.clear(); blobData.clear();
    	
        int ErodeCount = 0;
        int DilateCount = 0;
        
        IplImage BWImage = cvCreateImage(cvGetSize(RawImage), IPL_DEPTH_8U, 1);
        cvInRangeS(RawImage, minThresh, maxThresh, BWImage);
        
        IplImage WorkingImage = cvCreateImage(cvGetSize(BWImage), IPL_DEPTH_8U, 1);     
        cvErode(BWImage, WorkingImage, null, ErodeCount);    
        cvDilate(WorkingImage, WorkingImage, null, DilateCount);
        
        Blobs Regions = new Blobs();
        Regions.BlobAnalysis(
        		WorkingImage,               // image
                -1, -1,                     // ROI start col, row
                -1, -1,                     // ROI cols, rows
                1,                          // border (0 = black; 1 = white)
                MinArea);                   // minarea

        for(int i = 1; i <= Blobs.MaxLabel; i++) {
        	double [] Region = Blobs.RegionData[i];
        	int Parent = (int) Region[Blobs.BLOBPARENT];
        	int Color = (int) Region[Blobs.BLOBCOLOR];
            int MinX = (int) Region[Blobs.BLOBMINX];
            int MaxX = (int) Region[Blobs.BLOBMAXX];
            int MinY = (int) Region[Blobs.BLOBMINY];
            int MaxY = (int) Region[Blobs.BLOBMAXY];
            if (!((MaxX-MinX)>(RawImage.width()-50))) {
            	Highlight(RawImage,  MinX, MinY, MaxX, MaxY, 1);
                blobCent.add((MaxX+MinX)/2); blobCent.add((MaxY+MinY)/2);
                blobData.add(MinX); blobData.add(MinY); blobData.add(MaxX); blobData.add(MaxY);
            }
         }
        
        cvReleaseImage(BWImage);
        cvReleaseImage(WorkingImage);
        
        return RawImage;
    }
    
    public ArrayList<Integer> getCentres() {
    	return blobCent;
    }
    
    public ArrayList<Integer> getData() {
    	return blobData;
    }
    
    public static void Highlight(IplImage image, int [] inVec)
    {
        Highlight(image, inVec[0], inVec[1], inVec[2], inVec[3], 1);
    }
    public static void Highlight(IplImage image, int [] inVec, int Thick)
    {
        Highlight(image, inVec[0], inVec[1], inVec[2], inVec[3], Thick);
    }
    public static void Highlight(IplImage image, int xMin, int yMin, int xMax, int yMax)
    {
        Highlight(image, xMin, yMin, xMax, yMax, 1);
    }
    public static void Highlight(IplImage image, int xMin, int yMin, int xMax, int yMax, int Thick)
    {
        CvPoint pt1 = cvPoint(xMin,yMin);
        CvPoint pt2 = cvPoint(xMax,yMax);
        cvRectangle(image, pt1, pt2, CvScalar.WHITE, Thick, 4, 0);
        
        CvPoint cp = cvPoint((xMax+xMin)/2,(yMax+yMin)/2);
        cvCircle(image, cp, 2, CvScalar.WHITE, 10, CV_AA, 0);
    }
    
    public static void PrintGrayImage(IplImage image, String caption)
    {
        int size = 512; // impractical to print anything larger
        CvMat mat = image.asCvMat();
        int cols = mat.cols(); if(cols < 1) cols = 1;
        int rows = mat.rows(); if(rows < 1) rows = 1;
        double aspect = 1.0 * cols / rows;
        if(rows > size) { rows = size; cols = (int) ( rows * aspect ); }
        if(cols > size) cols = size;
        rows = (int) ( cols / aspect );
        PrintGrayImage(image, caption, 0, cols, 0, rows);
    }
    public static void PrintGrayImage(IplImage image, String caption, int MinX, int MaxX, int MinY, int MaxY)
    {
        int size = 512; // impractical to print anything larger
        CvMat mat = image.asCvMat();
        int cols = mat.cols(); if(cols < 1) cols = 1;
        int rows = mat.rows(); if(rows < 1) rows = 1;
        
        if(MinX < 0) MinX = 0; if(MinX > cols) MinX = cols; 
        if(MaxX < 0) MaxX = 0; if(MaxX > cols) MaxX = cols; 
        if(MinY < 0) MinY = 0; if(MinY > rows) MinY = rows; 
        if(MaxY < 0) MaxY = 0; if(MaxY > rows) MaxY = rows; 
        
        System.out.println("\n" + caption);
        System.out.print("   +");
        for(int icol = MinX; icol < MaxX; icol++) System.out.print("-");
        System.out.println("+");
        
        for(int irow = MinY; irow < MaxY; irow++)
        {
            if(irow<10) System.out.print(" ");
            if(irow<100) System.out.print(" ");
            System.out.print(irow);
            System.out.print("|");
            for(int icol = MinX; icol < MaxX; icol++)
            {
                int val = (int) mat.get(irow,icol);
                String C = " ";
                if(val == 0) C = "*";
                System.out.print(C);
            }
            System.out.println("|");
        }
        System.out.print("   +");
        for(int icol = MinX; icol < MaxX; icol++) System.out.print("-");
        System.out.println("+");
    }

    public static void PrintImageProperties(IplImage image)
    {
        CvMat mat = image.asCvMat();
        int cols = mat.cols();
        int rows = mat.rows();
        int depth = mat.depth();
        System.out.println("ImageProperties for " + image + " : cols=" + cols + " rows=" + rows + " depth=" + depth);
    }
    
    public static float BinaryHistogram(IplImage image)
    {
        CvScalar Sum = cvSum(image);
        float WhitePixels = (float) ( Sum.getVal(0) / 255 );
        CvMat mat = image.asCvMat();
        float TotalPixels = mat.cols() * mat.rows();
        return WhitePixels / TotalPixels;
    }
  
    public static IplImage SkewGrayImage(IplImage Src, double angle)    // angle is in radians
    {
        double sin = - Math.sin(angle);
        double AbsSin = Math.abs(sin);
        
        int nChannels = Src.nChannels();
        if(nChannels != 1) 
        {
            System.out.println("ERROR: SkewGrayImage: Require 1 channel: nChannels=" + nChannels);
            System.exit(1);
        }
        
        CvMat SrcMat = Src.asCvMat();
        int SrcCols = SrcMat.cols();
        int SrcRows = SrcMat.rows();

        double WidthSkew = AbsSin * SrcRows; 
        double HeightSkew = AbsSin * SrcCols;
        
        int DstCols = (int) ( SrcCols + WidthSkew ); 
        int DstRows = (int) ( SrcRows + HeightSkew );
    
        CvMat DstMat = cvCreateMat(DstRows, DstCols, CV_8UC1);  // Type matches IPL_DEPTH_8U
        cvSetZero(DstMat);
        cvNot(DstMat, DstMat);
        
        for(int irow = 0; irow < DstRows; irow++)
        {
            int dcol = (int) ( WidthSkew * irow / SrcRows );
            for(int icol = 0; icol < DstCols; icol++)
            {
                int drow = (int) ( HeightSkew - HeightSkew * icol / SrcCols );
                int jrow = irow - drow;
                int jcol = icol - dcol;
                if(jrow < 0 || jcol < 0 || jrow >= SrcRows || jcol >= SrcCols) DstMat.put(irow, icol, 255);
                else DstMat.put(irow, icol, (int) SrcMat.get(jrow,jcol));
            }
        }
        
        IplImage Dst = cvCreateImage(cvSize(DstCols, DstRows), IPL_DEPTH_8U, 1);
        Dst = DstMat.asIplImage();
        return Dst;
    }
    
    public static IplImage TransposeImage(IplImage SrcImage)
    {
        CvMat mat = SrcImage.asCvMat();
        int cols = mat.cols();
        int rows = mat.rows();
        IplImage DstImage = cvCreateImage(cvSize(rows, cols), IPL_DEPTH_8U, 1);
        cvTranspose(SrcImage, DstImage);
        cvFlip(DstImage,DstImage,1);
        return DstImage;
    }
}

