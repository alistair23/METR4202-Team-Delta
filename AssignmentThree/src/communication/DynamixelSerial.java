package communication;

import java.io.UnsupportedEncodingException;

public class DynamixelSerial {

	//int LENGTH_OF_PACKET  = 22;
	Serial srl = new Serial(4);
	
	public static void main(String[] args) {
		DynamixelSerial ds = new DynamixelSerial(4);
		//System.out.println(ds.calcChecksum("0104022B01")); //FFFF0104022B01CC
		ds.go(180, 180, 180);
	}
	
	
	
	public DynamixelSerial (int port){
		srl = new Serial(port);
	}

	 /** This program will construct a packet to be sent to the 
	 ** dynamixel motors for METR4202 **/
	
	public void command(int id, String instruction, String parameters){
		
	}
	
	public void ping(int id){
		
	}
	
	public void write(int id, int register, int value){
	
	}
	
	public void go(int one, int two, int three){
		

		
		
		srl.open();
		motor(3,100);
		//srl.write("FFFF0104022B01CC");
		//while(true){
		//srl.read();}
		//srl.close();
		
		//serial.out.send(char2byte(packets));
		
	}
	
	public void motor(int id, int pos){
		String ID = "03";//everything
		String length = "04"; //length of value VV
		String instruction = "03"; //write
		String register = "1E"; //goal position;
		String value = "01F9"; //about 500
		
		
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
			System.out.println(stringchecksum.toUpperCase());
			//lower byte of checksumstring
			
			return stringchecksum.toUpperCase();
	 }

}
