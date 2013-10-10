package capture;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openni.VideoFrameRef;
import org.openni.VideoStream;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class is used to convert from Kinect color and depth images in
 * VideoFrameRef form to JavaCV IplImage form.
 *
 */

public class ImageConverter {
	
	float mHistogram[];
	private VideoStream mVideoStream;
	
	public ImageConverter(){
	}
	
	public static void main(String[] args) {
		ImageConverter ic = new ImageConverter();
		ByteBuffer b = ic.loadBuffer("depthPixelData.xml");
		BufferedImage bi = ic.convertD(b);
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(640,480));
		JPanel j = new JPanel();
		
		j.add(new JLabel(new ImageIcon(bi)));
		
		f.getContentPane().add(j);
		f.setVisible(true);
	}
	
	public BufferedImage convertRGB(VideoFrameRef image){
		
		int[] packedPixels = new int[image.getWidth() * image.getHeight() * 3];
		 
        int bufferInd = 0;
        for (int row = 0; row <= image.getHeight()-1; row++) {
            for (int col = 0; col < image.getWidth(); col++) {
            	
                int R, G, B;
                R = image.getData().get(bufferInd++);
                G = image.getData().get(bufferInd++);
                B = image.getData().get(bufferInd++);
                
                int index =((row * image.getWidth() + col) * 3);
                
                packedPixels[index++] = R;
                packedPixels[index++] = G;
                packedPixels[index] = B;
            }
        }
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        WritableRaster wr = img.getRaster();
        wr.setPixels(0, 0, image.getWidth(), image.getHeight(), packedPixels);
        return img;
		
	}
	
	public IplImage convertRGB(BufferedImage image){
		return IplImage.createFrom(image);
	}
	
	public BufferedImage convertRGB(IplImage image){
		return image.getBufferedImage();
	}
	
	public BufferedImage convertD(VideoFrameRef image, VideoStream stream){

		mVideoStream = stream;
		int[] packedPixels = new int[image.getWidth() * image.getHeight()];
		  
		ByteBuffer frameData = image.getData().order(ByteOrder.LITTLE_ENDIAN);
	        
	    switch (image.getVideoMode().getPixelFormat()) {
	    
	            case DEPTH_1_MM:
	            case DEPTH_100_UM:
	            case SHIFT_9_2:
	            case SHIFT_9_3:
	                calcHist(frameData);
	                frameData.rewind();
	                int pos = 0;
	                while(frameData.remaining() > 0) {
	                    int depth = (int)frameData.getShort() & 0xFFFF;
	                    short pixel = (short)mHistogram[depth];
	                    packedPixels[pos] = 0xFF000000 | (pixel << 16) | (pixel << 8);
	                    pos++;
	                }
	                break;
	            case RGB888:
	                pos = 0;
	                while (frameData.remaining() > 0) {
	                    int red = (int)frameData.get() & 0xFF;
	                    int green = (int)frameData.get() & 0xFF;
	                    int blue = (int)frameData.get() & 0xFF;
	                    packedPixels[pos] = 0xFF000000 | (red << 16) | (green << 8) | blue;
	                    pos++;
	                }
	                break;
	             default:
	            	image.release();
	        }
	        
	        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
	        img.setRGB(0, 0, image.getWidth(), image.getHeight(), packedPixels, 0, image.getWidth());
	        return img;
	}
	
    private void calcHist(ByteBuffer depthBuffer) {
        // make sure we have enough room
        mHistogram = new float[mVideoStream.getMaxPixelValue()];
        
        // reset
        for (int i = 0; i < mHistogram.length; ++i)
            mHistogram[i] = 0;

        int points = 0;
        while (depthBuffer.remaining() > 0) {
            int depth = depthBuffer.getShort() & 0xFFFF;
            if (depth != 0) {
                mHistogram[depth]++;
                points++;
            }
        }

        for (int i = 1; i < mHistogram.length; i++) {
            mHistogram[i] += mHistogram[i - 1];
        }

        if (points > 0) {
            for (int i = 1; i < mHistogram.length; i++) {
                mHistogram[i] = (int) (256 * (1.0f - (mHistogram[i] / (float) points)));
            }
        }
    }
	
	public IplImage convertD(BufferedImage image){
		return IplImage.createFrom(image);
	}
	
	public BufferedImage convertD(IplImage image){
		return image.getBufferedImage();
	}
	
	public void savePNG(String str,BufferedImage img){
		File outputfile = new File(str+".png");
		try {
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {}
	}
	
	public void savePNG(String str,IplImage img){
		File outputfile = new File(str+".png");
					try {
						ImageIO.write(convertD(img), "png", outputfile);
					} catch (IOException e) {}
		
			try {
				ImageIO.write(convertRGB(img), "png", outputfile);
			} catch (IOException e) {}
		
	}
	
	int little2big(int i) {
	    return((i&0xff)<<24)+((i&0xff00)<<8)+((i&0xff0000)>>8)+((i>>24)&0xff);
	}
	
	public void saveBuffer(ByteBuffer bb){
		try {
			File file = new File("out.xml");
			FileChannel channel = new FileOutputStream(file, false).getChannel();
			bb.flip();
			channel.write(bb);
			channel.close();
		}
		catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
	}
	
	public ByteBuffer loadBuffer(String str){
		ByteBuffer buf = null;
		try {
			
			File inFile = new File(str);

			// Allocate a direct (memory-mapped) byte buffer with a byte capacity equal to file's length
			// DO NOT use this approach for copying large files
			buf = ByteBuffer.allocateDirect((int)inFile.length());

			InputStream is = new FileInputStream(inFile);

		    int b;

		    while ((b=is.read())!=-1) {
		    	buf.put((byte)b);
		    }
		}
		catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		buf.flip();
		return buf;

	}
	
	   public BufferedImage convertD(ByteBuffer frame) {
	        
	        // make sure we have enough room

		   ByteBuffer frameData = frame.order(ByteOrder.LITTLE_ENDIAN);
		   int width = 640;
		   int height = 480;
			
		   int[] packedPixels = new int[width * height * 3];
		        frameData.rewind();
		        int pos = 0;
		        while(frameData.remaining() > 0) {
		             int depth = (int)frameData.getShort() & 0xFFFF;
		             packedPixels[pos] = depth;
		             pos++;
		         }
		
		    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		    WritableRaster wr = img.getRaster();
		    wr.setPixels(0, 0, width, height, packedPixels);
		    return img;
	    }
}
