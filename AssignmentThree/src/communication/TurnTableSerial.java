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
	

	static Serial srl = new Serial(5);
	
	public static void main(String[] args) {
		srl.open(9600);

		TurnTableSerial ts = new TurnTableSerial();
		
		//ts.readSpeed();
		//ts.srl.read();

		//ts.writeSpeed();
		//ts.getMoving();
		//ts.writeSpeed(900);
		ts.writeTarget(1530);
		//srl.read();
	}
	
	public void readSpeed(){
		srl.write("9003");
		srl.read();
	}
	
	public void writeSpeed(int speed){
		System.out.println(xval(speed));
		srl.write("8703"+xval(speed));
	}
	
	public void writeTarget(int target){
		System.out.println(xval(target));
		srl.write("FF03"+xval(target));
	}

	public void writeTarget(String target){
		System.out.println(target);
		srl.write("8403"+target);
	}

	public void getMoving(){
		srl.write("9003");
		srl.read();
	}
	
	 public String xval(int value){
		 
		String str = Integer.toString(value, 16);
		while(str.length() < 4){
			str = "0".concat(str);
		}
		if(str.length() == 4){
		str = str.substring(2, 4)+str.substring(0, 2);
		}
		 
		 return str.toUpperCase();
		 
		 
	 }

}
