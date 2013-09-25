package capture;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.openni.PixelFormat;
import org.openni.VideoFrameRef;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageConverter {
	
	public ImageConverter(){
	}
	
	public BufferedImage convertRGB(VideoFrameRef image){
		
		int[] packedPixels = new int[image.getWidth() * image.getHeight() * 3];
		 
        int bufferInd = 0;
        for (int row = 0; row <= image.getHeight()-1; row++) {
            for (int col = 0; col < image.getWidth(); col++) {
            	
            	//System.out.println(width+" "+height+" "+row+" "+col);
            	
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
	
	public BufferedImage convertD(VideoFrameRef image){
		//TODO read the data as little-endian to get a smooth image
		  int[] packedPixels = new int[image.getWidth() * image.getHeight() * 3];
			 
		  ByteBuffer pixels = image.getData();
		  
	        int bufferInd = 0;
	        for (int row = 0; row <= image.getHeight() - 1; row++) {
	            for (int col = 0; col < image.getWidth(); col++) {
	            	
	            
	            	
	            	//System.out.println(width+" "+height+" "+row+" "+col);
	            	
	                int L, M;

	                M = pixels.get(bufferInd++);
	                L = pixels.get(bufferInd++);
	                
	                int index = (row * image.getWidth() + col) *1;
	                packedPixels[index] = M;
	                packedPixels[index++] = L;

	                
	            }
	        }
	        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
	        WritableRaster wr = img.getRaster();
	        wr.setPixels(0, 0, image.getWidth(), image.getHeight(), packedPixels);
	        return img;
		
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

	public void savePNG(String str,VideoFrameRef img){
		File outputfile = new File(str+".png");

		if(img.getVideoMode().getPixelFormat() == PixelFormat.DEPTH_1_MM || 
				img.getVideoMode().getPixelFormat() == PixelFormat.DEPTH_100_UM){
					try {
						ImageIO.write(convertD(img), "png", outputfile);
					} catch (IOException e) {}
		}
		else{
			try {
				ImageIO.write(convertRGB(img), "png", outputfile);
			} catch (IOException e) {			}
		}

	}
	
	int little2big(int i) {
	    return((i&0xff)<<24)+((i&0xff00)<<8)+((i&0xff0000)>>8)+((i>>24)&0xff);
	}

}
