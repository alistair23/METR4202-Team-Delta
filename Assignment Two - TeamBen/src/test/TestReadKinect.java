package test;

import capture.KinectReader;

public class TestReadKinect {
	
	public static void main(String s[]) {
	
  		KinectReader kr = new KinectReader();
				
		kr.Start();
		
		kr.getFrames();
		
		kr.showStreams();
		
	}
	
	
}
