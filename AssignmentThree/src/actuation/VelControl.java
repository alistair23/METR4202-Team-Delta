package actuation;

import communication.DynamixelSerial;

public class VelControl {

	static DynamixelArm da = new DynamixelArm();
	
	static Thread t = new Thread(da);
	public static void main(String[] args) {
		t.start();
		
		

		
		

		for(int i=80;i<150;i+=10){
			da.x = i;
			da.y = 150;
			da.speed = 100;
			DynamixelSerial.halt(100);
		}
			


		
	}
	
}
