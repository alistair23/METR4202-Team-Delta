package communication;

import gnu.io.*;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TurnTableComm {
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	
	OutputStream out;
	InputStream in;
	public TurnTableComm() {
		super();
	}
	
	static void listPorts()
	{
	    java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
	    while ( portEnum.hasMoreElements() ) 
	    {
	        CommPortIdentifier portIdentifier = portEnum.nextElement();
	        System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
	    }        
	}

	static String getPortTypeName ( int portType )
	{
	    switch ( portType )
	    {
	        case CommPortIdentifier.PORT_I2C:
	            return "I2C";
	        case CommPortIdentifier.PORT_PARALLEL:
	            return "Parallel";
	        case CommPortIdentifier.PORT_RAW:
	            return "Raw";
	        case CommPortIdentifier.PORT_RS485:
	            return "RS485";
	        case CommPortIdentifier.PORT_SERIAL:
	            return "Serial";
	        default:
	            return "unknown type";
	    }
	}

	void connect(String portName) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					TIME_OUT);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(DATA_RATE,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				 in = serialPort.getInputStream();
				 out = serialPort.getOutputStream();

				(new Thread(new SerialReader(in))).start();
				(new Thread(new SerialWriter(out))).start();

			} else {
				System.out
						.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	/** */
	public static class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void run() {
			byte[] low = new byte[1024];
			byte[] high = new byte[1024];
			byte[] trial = new byte[1];
			int len = -1;
			try {
			//	while ((len = this.in.read(low)) > -1 && ((len = this.in.read(high)) > -1)) {
					//System.out.println(new String(low, 0, len));
					//System.out.println(new String(high, 0, len));
				while ((len = this.in.read(trial)) > -1) {
					//System.out.println(trial[0]);
					//System.out.println(trial[1]);
					
					//System.out.println(((double)twoBytesToShort(trial[0], trial[1])));
					System.out.println(trial[0]);
				}
					//System.out.format("Length of data read is %d.\n", len);
			//	}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static short twoBytesToShort(byte b1, byte b2) {
        return (short) (((b1 << 8) | (b2 & 0xFF))&(0b0111111111111111));
	}

	/** */
	public static class SerialWriter implements Runnable {
		OutputStream out;

		public SerialWriter(OutputStream out) {
			this.out = out;
		}

		public void run() {
			int buffer,channel;
			try {
				buffer = 0x90;
				channel = 3;
				while (buffer != 0) {
					this.out.write((byte) buffer);
					this.out.write((byte) channel);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		TurnTableComm Sc = new TurnTableComm();
		try {
			/**try to connect with serial port*/
			Sc.connect("COM5");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TurnTableComm.listPorts();
		SerialReader Sr = new SerialReader(Sc.in);
		SerialWriter Sw = new SerialWriter(Sc.out);
		Sw.run();
		Sr.run();
		
	}
}