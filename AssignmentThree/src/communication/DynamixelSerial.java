package communication;

import java.io.UnsupportedEncodingException;

public class DynamixelSerial {

	//int LENGTH_OF_PACKET  = 22;
	Serial srl = new Serial(3);
	
	public static void main(String[] args) {
		DynamixelSerial ds = new DynamixelSerial(3);
		//System.out.println(ds.calcChecksum("0104022B01")); //FFFF0104022B01CC
		
		
		ds.motor(1,50,302);
		//halt(1);
		ds.motor(2,90,382);
		//halt(1);
		ds.motor(3,50,180);

		halt(1100);
		ds.motor(1,-50,302);
		//halt(5);
		ds.motor(2,-90,382);
		//halt(5);
		ds.motor(3,-50,180);
		
		halt(1100);
		ds.centermotors();
	}
	
	public static void halt(int milli){
		try {Thread.sleep(milli);} 
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public DynamixelSerial (int port){
		srl = new Serial(port);
		srl.open();
	}

	 /** This program will construct a packet to be sent to the 
	 ** dynamixel motors for METR4202 **/
	
	public void command(int id, String instruction, String parameters){
		
	}
	
	public void ping(int id){
		
	}
	
	public void write(String id,String length, String register, String parameters){
		String command = id+length+"03"+register+parameters;
		
		command = command + calcChecksum(command);
		
		srl.write("FFFF"+command); //FFFF is the GO command
		
		//while(true){
		//	srl.read();
		//}
	}
	

	
	public void motor(int id, int pos){
		if(id==0){write("FE","05","1E",xval(pos));} 
		else{
			write("0"+id,"05","1E",xval(pos));
		}
	}
	
	public void motor(int id, int pos, int speed){

		int Apos = (int) ((((float)pos)+150.0)*512.0/150.0);
		if(Apos == 0 ){Apos = 512;}
		//System.out.println(Apos);
		
		if(id==0){write("FE","07","1E",xval(Apos)+xval(speed));} 
		else{
			write("0"+id,"07","1E",xval(Apos)+xval(speed));
		}
	}
	
	
	public void centermotors(){
		String ID = "FE";//everything
		String length = "07"; //length of value VV including register
		String instruction = "03"; //write
		String register = "1E"; //goal position;
		String value = "00020002"; //about Centre them
		
		
		String command = ID+length+instruction+register+value;
		
		command = command + calcChecksum(command);
		
		srl.write("FFFF"+command); //FFFF is the GO command

	}
	
	

	
	 public static byte[] char2byte(char[] buffer) {
		 byte[] b = new byte[buffer.length];
		 for (int i = 0; i < b.length; i++) {
		  b[i] = (byte) buffer[i];
		 }
		 return b;
		}
	 
	 public String calcChecksum(String command){
		//calc checksum
		 int checksum = 0;
			for(int i = 0; i < command.length(); i+=2) {
				String s = command.substring(i, i+2); 
				checksum += Integer.parseInt(command.substring(i, i+2), 16);;
			}
			//checksum = Integer.toBinaryString(~checksum);
			
			String stringchecksum = Integer.toBinaryString(~checksum); //checksum as hex string
			stringchecksum = Long.toHexString(Long.parseLong(stringchecksum, 2));
			stringchecksum = stringchecksum.substring(stringchecksum.length()-2, stringchecksum.length());
			//System.out.println(stringchecksum.toUpperCase());
			//lower byte of checksumstring
			
			return stringchecksum.toUpperCase();
	 }
	 public String xval(int v){
		 
		String str = Integer.toString(v, 16);
		while(str.length() < 4){
			str = "0".concat(str);
		}
		if(str.length() == 4){
		str = str.substring(2, 4)+str.substring(0, 2);
		}
		 
		 return str;
		 
		 
	 }
	 //public void wait(){
		 
	// }

}
