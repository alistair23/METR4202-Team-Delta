package actuation;

import communication.DynamixelSerial;
import communication.Serial;

public class DynamixelArm {

	//Constants
	double L1 = 144; //length of member 1 in mm
	double L2 = 83; //length of member 2 in mm
	double L3 = 35; //length of member 3 in mm
	
	//Serial comm port for USB2Dynamixel
	int port  = 3;
	
	int motor1ID = 1;
	int motor2ID = 2;
	int motor3ID = 3;
	
	//variables
	double x1,x2,x3,x; //x lengths
	double y1,y2,y3,y; //y lengths
	double a1,a2,a3; //angles
	double direction = 1; //direction = -1 for other side of arm. direction = 1 for turntable side
	
	DynamixelSerial ds = new DynamixelSerial(port);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DynamixelArm da = new DynamixelArm();
		/**
		da.setAng(0, 90);
		System.out.println("SET: a1: "+da.a1+"  a2: "+da.a2+"  a3: "+da.a3);
		System.out.println("x1: "+da.x1+"  x2: "+da.x2);
		System.out.println("y1: "+da.y1+"  y2: "+da.y2);
		System.out.println("x: "+da.x+"  y: "+da.y);
		System.out.println("**************************************");
**/
		da.calcAng(50.0, 200.0);
		//da.setAng(0, 90);
		
		System.out.println("CALC: a1: "+da.a1+"  a2: "+da.a2+"  a3: "+da.a3);
		System.out.println("x1: "+da.x1+"  x2: "+da.x2);
		System.out.println("y1: "+da.y1+"  y2: "+da.y2);
		System.out.println("x: "+da.x+"  y: "+da.y);
		//da.motor(3,50,180);
	}
	
	public DynamixelArm(){
		
		
		//ds.motor(1,50,302);
	}
	
	public double x(double one, double two){
		a1 = one;
		a2 = two;
		x1 = L1*cos(a1);
		x2 = L2*cos(a1+a2);
		x = Math.abs(x1)+Math.abs(x2);
		return x;
	}

	public double y(double one, double two){
		a1 = one;
		a2 = two;
		y1 = L1*sin(a1);
		y2 = L2*sin(a1+a2);
		if(y2<y1){y2=-y2;}
		y = y1+y2;
		return y;
	}

	public void calcxy(double one, double two){
		a3 = 90+a1 + a2;
		x(one, two);
		y(one, two);
		a3 = 180-a1 - a2;
		y=y-L3;
		
		a1 = direction*a1;
		a2 = direction*a2;
		a3 = direction*a3;
	}
	
	public void calcAng(double x, double y){
		this.x = x;
		this.y = y;
		double d = Math.sqrt((x*x)+(y*y));	
		//d= 100;
		a1 = Math.toRadians(90.0) - Math.acos(((L1*L1)+(d*d)-(L2*L2))/(2*L1*d)) - Math.acos(x/d);
		a2 = Math.toRadians(180.0) - Math.acos(((L1*L1)+(L2*L2)-(d*d))/(2*L1*L2));
		a3 = Math.toRadians(180.0) - a1 - a2;
		
		a1 = Math.toDegrees(a1);
		a2 = Math.toDegrees(a2);
		a3 = Math.toDegrees(a3);
		
		ds.motor(motor1ID, (int)a1, 100);
		ds.motor(motor2ID, (int)a2, 100);
		ds.motor(motor3ID, (int)a3, 100);
	}
	
	public void setPos(double x, double y){
		
	}
	
	public void setAng(double one, double two){
		calcxy(one,two);
		ds.motor(motor1ID, (int)a1, 300);
		ds.motor(motor2ID, (int)a2, 300);
		ds.motor(motor3ID, (int)a3, 300);
	}
	

	//have a flag that makes all the angles posotive or negative depending on the desired side.
	
	//get angles 1 and angle 2 first to get x and y, tool tip is jus offset from this point.
	
	//angle 3 is just whatever it needs to be to be vertical.
	
	
	public double cos(double a){
		double r = Math.cos(Math.toRadians(a));
		if(a==0){r = 0;}
		if(a==90){r = 1;}
		return r;
	}
	public double sin(double a){
		double r = Math.sin(Math.toRadians(a));
		if(a==0){r = 1;}
		if(a==90){r = 0;}
		return r;
	}
	
	
	
}
