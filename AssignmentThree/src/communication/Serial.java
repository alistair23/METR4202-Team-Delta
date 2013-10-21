package communication;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import jssc.SerialPortList;
import jssc.SerialPort;
import jssc.SerialPortException;


public class Serial {
	
	SerialPort serialPort;
	
	public Serial(int p){
		serialPort = new SerialPort("COM"+p);
	}
   
	 public static void main(String[] args) {
		 Serial s = new Serial(4);
		// s.getNames();
		 s.open();
		 //s.write("FFFF0104022B01CC"); //return temp of actuator 1
		 //s.write("FFFFFE18831E04001000500101200260030230007001032002800312"); //FFFFFE18831E04001000500101200260030230007001032002800312
		 s.write("FFFF0104022B01CC");
		 
		 while(true){
			 s.read();
		//	 s.write("FF");
		 }
		 
		// s.close();
	    }
	
	    public  void getNames() {
	        String[] portNames = SerialPortList.getPortNames();
	        for(int i = 0; i < portNames.length; i++){
	            System.out.println(portNames[i]);
	        }
	    }
	    
	    public  void open() {
	    	 try {
	                serialPort.openPort();//Open serial port
	                serialPort.setParams(1000000, 
	                                     SerialPort.DATABITS_8,
	                                     SerialPort.STOPBITS_1,
	                                     SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
	    	 }
	            catch (SerialPortException ex) {
	                System.out.println(ex);
	            }
	    }
	    
	    public  void close() {
	    	 try {
	                serialPort.closePort();//Close serial port
	            }
	            catch (SerialPortException ex) {
	                System.out.println(ex);
	            }
	    }
	    
    
    
    public void write(String s) {
           // SerialPort serialPort = new SerialPort("COM1");
            try {
            	//System.out.println("Writing: "+s);
            	serialPort.writeBytes(hexStringToByteArray(s));//Write data to port
            }
            catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
    
        public void read() {
            //SerialPort serialPort = new SerialPort("COM1");
            try {
            	int c;
            	if ((serialPort.getInputBufferBytesCount()) != 0){
            	
                 //byte[] buffer = serialPort.readBytes(serialPort.getInputBufferBytesCount());//Read 10 bytes from serial port
                 String buffer = serialPort.readHexString();
            		System.out.println(buffer);
            	}
             }
            catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
        
        public static byte[] hexStringToByteArray(String s) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                     + Character.digit(s.charAt(i+1), 16));
            }
            return data;
        }
        
        public String bytArrayToHex(byte[] a) {
  
        	
        	 /**byte[] bytes = {-1, 0, 1, 2, 3 };
        	    StringBuilder sb = new StringBuilder();
        	    for (byte b : a) {
        	        sb.append(String.format("%02X", b));
        	    }
        	    return sb.toString();
        	    // prints "FF 00 01 02 03 "**/
        	String s = "";
        	try {
				 s = new String(a, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return s;
        		
        }
        
    }
    
    
    
    
    
