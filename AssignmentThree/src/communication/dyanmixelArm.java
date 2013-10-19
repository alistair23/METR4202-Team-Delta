package communication;

public class dyanmixelArm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	int LENGTH_OF_PACKET  = 22;

	 /** This program will construct a packet to be sent to the 
	 ** dynamixel motors for METR4202 **/
	
	 public char[] construct_packet(int one, int two, int three) {
		/* NOTE: x, y and z must be between 0 and 1023 decimal.
		** This program does not check the values */
		
		/* NOTE: speed must be between 0 and 1023 decimal.
		** This program does not check the values */
		int speed = 32;
		int i, checksum;
		int[] angles = new int[3];
		angles[0] = one;
		angles[1] = two;
		angles[2] = three;

		char[] packet = new char[LENGTH_OF_PACKET];
		
		// The two starting bytes
		packet[0] = 0xFF;
		packet[1] = 0xFF;
		
		// The ID of the packet
		packet[2] = 0xFE;
		
		// The length of the packet
		// Calculated using (L+1)*N + 4
		packet[3] = 0x13;
		
		// The sync_write instruction
		packet[4] = 0x83;
		
		// The write address of the goal position
		packet[5] = 0x1E;
		
		// The length of each command sent to the Dynamixel
		packet[6] = 0x04;
		
		for(i = 0; i < 3; i++) {
			// Store the ID of the sepcific dynamixel
			packet[7 + (i*5)] = (char) i;
			
			// Store the LSB of the positioin
			packet[8 + (i*5)] = (char) (angles[i] & 0xFF);
			
			// Store the MSB of the positioin
			packet[9 + (i*5)] = (char) ((angles[i] << 8) & 0xFF);
			
			// Store the LSB of the speed
			packet[10 + (i*5)] = (char) (speed & 0xFF);
			
			// Store the MSB of the speed
			packet[11 + (i*5)] = (char) ((speed << 8) & 0xFF);
		}
		
		// Calculate the checksum
		checksum = 0;
		
		// Sum all of the packet (except for the first two IDs
		for(i = 2; i < 22; i++) {
			checksum += packet[i];
		}
		
		// Inverse the packet and keep the lower byte
		char chsm = (Integer.toBinaryString(~checksum)).toCharArray()[0];
		packet[22] = (char) (chsm & 0xFF);
		
		return packet;
	}
	

}
