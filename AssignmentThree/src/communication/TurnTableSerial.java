package communication;

public class TurnTableSerial {
	
	/**
	 * script for the maestro control
	 * 
begin
  0 get_position    # get the value of the pot, 0-1023
  4 times 4000 plus # scale it to 4000-8092, approximately 1-2 ms
  3 servo           # set servo 0 based to the value
repeat
	 */
	

	static Serial srl = new Serial(4);
	
	public TurnTableSerial() {
		srl.open(9600);
	}
	
	public static void main(String[] args) {
		
		TurnTableSerial ts = new TurnTableSerial();
		
		ts.xval(1500);
		
		//ts.readSpeed();
		//ts.srl.read();

		//ts.writeSpeed();
		//ts.getMoving();
		//ts.writeSpeed(112);
		ts.writeTarget(1123);
		//srl.read();
		
		//while (true) {
		//	System.out.println(ts.readSpeed());
		//}
		srl.close();
	}
	
	// returns speed in rad/s
	public Double readSpeed(){
		srl.write("9003");
		String readString = srl.read();
		Integer speed = -Integer.parseInt(readString.substring(3, 5)+readString.substring(0, 2), 16)+6000;
		// max = +-16 rpm
		// 2000 = (16*2*Math.PI/60)
		//double mult = Math.PI/3750.0;
		double mult = Math.PI/2000.0;
		return speed*mult;
	}
	
	public void writeSpeed(int speed){
		System.out.println(xval(speed));
		srl.write("8703"+xval(speed));
	}
	
	public void writeTarget(int target){
		System.out.println(xval(target));
		srl.write("FF03"+xval(target));
	}

	//public void writeTarget(String target){
	//	System.out.println(target);
//		srl.write("8403"+target);
//	}

	public void getMoving(){
		srl.write("9003");
		srl.read();
	}
	
	 public String xval(int value){
		int val = value*4;
		String bi = Integer.toBinaryString(val);
		
		
		while(bi.length() < 14){
			bi = "0".concat(bi);
		}
		String bi1 = bi.substring(0,7);
		while(bi1.length() < 8){
			bi1 = "0".concat(bi1);
		}

		String bi2 = bi.substring(7,14);
		while(bi2.length() < 8){
			bi2 = "0".concat(bi2);
		}
		
		bi1 = Integer.toHexString(Integer.parseInt(bi1, 2));
		bi2 = Integer.toHexString(Integer.parseInt(bi2, 2));
		
		
		
		System.out.println(bi1);
		System.out.println(bi2);
		
		 
		 String str = bi2+bi1;
		while(str.length() < 4){
			str = "0".concat(str);
		}
		
		System.out.println(str.toUpperCase()); 
		 return str.toUpperCase();
		 
		 
	 }

}
