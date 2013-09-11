public class Lab2 {
	
	public static void main(String s[]) {
	
		KinectReader kr = new KinectReader();
		
		kr.Start();
		
		kr.getFrame();
		
		kr.showFrame();
		
		for(int i=0; i<kr.Dframe.getHeight()*kr.Dframe.getWidth()*100000;i++){
			//System.out.println(kr.Dframe.getData().getInt(i));
			//System.out.println(kr.Dstream.readFrame().getFrameIndex());
		}
		
	}
	
	
}
