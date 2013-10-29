package actuation;

import communication.DynamixelSerial;

public class PIDMotor {

	//Angle needs to be in 0 to 300, no negative angles coming in
	int PrevError; //Motor previous error (Degrees)
	int IntegralError=0; //Motor integral error (Degrees)
	static int CurrentMotorAngle = 0; //Motor angle (Degrees) read in at set sample time
	static int Target = 180; //Target Angle
	static int MotorSpeed = 0;
	
	static DynamixelSerial ds;
	
	public static void main(String[] args) {

		(new Thread(ds = new DynamixelSerial(3))).start();

		PIDMotor M1 = new PIDMotor();

		ds.motor(2, -90, 500);
		while(ds.readMoving()){}

		do{
			
			CurrentMotorAngle = ds.readPos("02", "02");
			System.out.print(CurrentMotorAngle+"   ");
			CurrentMotorAngle = Math.abs((int)CurrentMotorAngle+90);
			System.out.println(CurrentMotorAngle);
			
			M1.doPID();
			
			System.out.println("moving motor");
			
			ds.motor(2, Target-90, MotorSpeed);
			DynamixelSerial.halt(10);
			System.out.println("Target:"+CurrentMotorAngle+"/"+Target);
			System.out.println("Speed:"+MotorSpeed);
			
		} while (Target - CurrentMotorAngle != 0);
	}

	void doPID(){ 
		
	int Error; //Error in degrees
	int DerError; //Derivative of Error
	int C; //Constant to add to speed
	double KP = 1; //PID proportional gain constant 
	double KD = 0.0; //PID derivative gain constant
	double KI = 0.0; //PID intergral gain constant (no integral control)

	Error = Target - CurrentMotorAngle; //Calculate error values
	System.out.println("ERROR:"+Error);
	//CurrentMotorAngle = 0; //Reset Angle ready for next sample
	DerError = (Error - PrevError); 
	C = (int)((KP*Error) + (KD*DerError) + (KI*IntegralError)); //PID equations

	if((C) > 1024){ //prevent MotorSpeed from overrunning  from 1024
		MotorSpeed = 1024;}
		else{
		MotorSpeed = C;} //Use output from PID equations to alter motor speeds 

	PrevError = Error; // Set previous error to current error
	IntegralError = IntegralError + Error; // Add current error to integral error
	}
}
