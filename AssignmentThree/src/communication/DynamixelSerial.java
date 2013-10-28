package communication;

public class DynamixelSerial {

	//Instruction
	static String PING = "01";
	static String READ_DATA = "02";
	static String WRITE_DATA = "03";
	static String REG_WRITE = "04";
	static String ACTION = "05";
	static String RESET = "06";
	static String SYNC_WRITE = "83";
	
	//Registers
	String[] RegName = new String[24];
	String[] RegVal = new String[24];
	int[] RegLength = new int[24];
	int[] RegMin = new int[24];
	int[] RegMax = new int[24];
	
	Serial srl = new Serial(3);
	
	public static void main(String[] args) {
		DynamixelSerial ds = new DynamixelSerial(3);
		//System.out.println(ds.calcChecksum("0104022B01")); //FFFF0104022B01CC
		
		ds.motor(1, 0, 100);
		ds.motor(2, 90, 100);
		ds.motor(3, 90, 100);

		
		ds.command("FE","05",WRITE_DATA,"1E","1301");
		for(int i = 0;i<1000;i++){
			boolean n = ds.readMoving();
			System.out.println(n);
		}
		
	}
	
	public static void halt(int milli){
		try {Thread.sleep(milli);} 
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public DynamixelSerial (int port){
		//Registers
		RegName[0] = "ID";						 	 RegVal[0] = "03";	 RegLength[0] = 1;	 RegMin[0] = 0;   RegMax[0] = 253;
		 RegName[1] = "Baud_Rate";					 RegVal[1] = "04";	 RegLength[1] = 1;	 RegMin[1] = 0;   RegMax[1] = 254;
		 RegName[2] = "Return_Delay_Time";			 RegVal[2] = "05";	 RegLength[2] = 1;	 RegMin[2] = 0;   RegMax[2] = 254;
		 RegName[3] = "CW_Angle_Limit";				 RegVal[3] = "06";	 RegLength[3] = 2;	 RegMin[3] = 0;   RegMax[3] = 1023;
		 RegName[4] = "CCW_Angle_Limit";			 RegVal[4] = "08";	 RegLength[4] = 2;	 RegMin[4] = 0;   RegMax[4] = 1023;
		 RegName[5] = "Highest_Limit_Temperature";	 RegVal[5] = "0B";	 RegLength[5] = 1;	 RegMin[5] = 0;   RegMax[5] = 150;
		 RegName[6] = "Lowest_Limit_Voltage";		 RegVal[6] = "0C";	 RegLength[6] = 1;	 RegMin[6] = 50;  RegMax[6] = 250;
		 RegName[7] = "Highest_Limit_Voltage";		 RegVal[7] = "0D";	 RegLength[7] = 1;	 RegMin[7] = 50;  RegMax[7] = 250;
		 RegName[8] = "Max_Torque";					 RegVal[8] = "0E";	 RegLength[8] = 2;	 RegMin[8] = 0;   RegMax[8] = 1023;
		 RegName[9] = "Status_Return_Level";		 RegVal[9] = "10";	 RegLength[9] = 1;	 RegMin[9] = 0;   RegMax[9] = 2;
		RegName[10] = "Alarm_LED";					RegVal[10] = "11";	RegLength[10] = 1;	RegMin[10] = 0;  RegMax[10] = 127;
		RegName[11] = "Alarm_Shutdown";				RegVal[11] = "12";	RegLength[11] = 1;	RegMin[11] = 0;  RegMax[11] = 127;
		RegName[12] = "Torque_Enable";				RegVal[12] = "18";	RegLength[12] = 1;	RegMin[12] = 0;  RegMax[12] = 1;
		RegName[13] = "LED";						RegVal[13] = "19";	RegLength[13] = 1;	RegMin[13] = 0;  RegMax[13] = 1;
		RegName[14] = "CW_Compliance_Margin";		RegVal[14] = "1A";	RegLength[14] = 1;	RegMin[14] = 0;  RegMax[14] = 254;
		RegName[15] = "CCW_Compliance_Margin";		RegVal[15] = "1B";	RegLength[15] = 1;	RegMin[15] = 0;  RegMax[15] = 254;
		RegName[16] = "CW_Compliance_Slope";		RegVal[16] = "1C";	RegLength[16] = 1;	RegMin[16] = 1;  RegMax[16] = 254;
		RegName[17] = "CCW_Compliance_Slope";		RegVal[17] = "1D";	RegLength[17] = 1;	RegMin[17] = 1;  RegMax[17] = 254;
		RegName[18] = "Goal_Position";				RegVal[18] = "1E";	RegLength[18] = 2;	RegMin[18] = 0;  RegMax[18] = 1023;
		RegName[19] = "Moving_Speed";				RegVal[19] = "20";	RegLength[19] = 2;	RegMin[19] = 0;  RegMax[19] = 1023;
		RegName[20] = "Torque_Limit";				RegVal[20] = "22";	RegLength[20] = 2;	RegMin[20] = 0;  RegMax[20] = 1023;
		RegName[21] = "Registered_Instruction";		RegVal[21] = "2C";	RegLength[21] = 1;	RegMin[21] = 0;  RegMax[21] = 1;
		RegName[22] = "Lock";						RegVal[22] = "2F";	RegLength[22] = 1;	RegMin[22] = 1;  RegMax[22] = 1;
		RegName[23] = "Punch";						RegVal[23] = "30";	RegLength[23] = 2;	RegMin[23] = 0;  RegMax[23] = 1023;
		
		
		srl = new Serial(port);
		srl.open();
	}

	 /** This program will construct a packet to be sent to the 
	 ** dynamixel motors for METR4202 **/
	
	public void command(String id,String length, String instruction, String register, String parameters){
		String command = id+length+instruction+register+parameters;
		
		command = command + calcChecksum(command);
		
		srl.write("FFFF"+command); //FFFF is the GO command
	}
	
	public void read(String id, String register){
		srl.read();
		command(id,"04","02",register,"01");
		System.out.println(srl.read());

	}
	
	public int readGoal(String id){
		
		command(id,"04","02","1E","02");
		String val = srl.read();
		halt(5);
		//val = val.substring(15, val.length()-3);
		val = val.substring(18, 20)+val.substring(15, 17);
		//System.out.println(val);
		return Integer.parseInt(val, 16);

	}
	
	public int readPos(String id){
		command(id,"04","02","24","02");
		String val = srl.read();
		halt(5);
		//val = val.substring(15, val.length()-3);
		val = val.substring(18, 20)+val.substring(15, 17);
		//System.out.println(val);
		int pos = Integer.parseInt(val, 16);
		//int Apos = (int) ((((float)pos)+150.0)*512.0/150.0);
		int Apos = (int) (pos*150.0/512.0)-150;
		return Apos;

	}
	
	public boolean readMoving(){
		
		
		command("01","04","02","2E","01");
		halt(5);
		String val1 = srl.read();
		val1 = val1.substring(15, 18);
		Boolean b1 = (val1.charAt(1)=='1');
		if(b1){
			return true;
		}
		command("02","04","02","2E","01");
		halt(5);
		val1 = srl.read();
		val1 = val1.substring(15, 18);
		b1 = (val1.charAt(1)=='1');
		if(b1){
			return true;
		}
		command("03","04","02","2E","01");
		halt(5);
		val1 = srl.read();
		val1 = val1.substring(15, 18);
		b1 = (val1.charAt(1)=='1');
		if(b1){
			return true;
		}
		
		
		return false;

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
	 public String xval(int value, int length){
		 
		String str = Integer.toString(value, 16);
		while(str.length() < length){
			str = "0".concat(str);
		}
		if(str.length() == 4){
		str = str.substring(2, 4)+str.substring(0, 2);
		}
		 
		 return str;
		 
		 
	 }
	 public String xval(int value){
		 
		String str = Integer.toString(value, 16);
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
