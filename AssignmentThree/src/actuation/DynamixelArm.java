package actuation;

import java.io.IOException;

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
		DynamixelArm da = new DynamixelArm();

		//da.flip(true);
		da.setXY(150, 150, 300);

	}
	
	public DynamixelArm(){
	}
	


	public void calcxy(double one, double two){
		a1 = one;
		a2 = two;
		
		x1 = L1*cos(a1);
		x2 = L2*cos(a1+a2);
		x = Math.abs(x1)+Math.abs(x2);

		y1 = L1*sin(a1);
		y2 = L2*sin(a1+a2);
		if(y2<y1){y2=-y2;}
		y = y1+y2;
		y=y-L3;
		
		a3 = 180-a1 - a2;
		
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
		
		a1 = direction*a1;
		a2 = direction*a2;
		a3 = direction*a3;
		
	}
	
	public void setAng(double ang1, double ang2, int speed){
		calcxy(ang1,ang2);
		ds.motor(motor1ID, (int)a1, speed);
		ds.motor(motor2ID, (int)a2, speed);
		ds.motor(motor3ID, (int)a3, speed);
	}
	
	public void setXY(double x, double y, int speed){
		calcAng(x,y);
		ds.motor(motor1ID, (int)a1, speed);
		ds.motor(motor2ID, (int)a2, speed);
		ds.motor(motor3ID, (int)a3, speed);
	}
	
	public void flip(boolean b){
		if(b == true ){direction = -1;}
		else{direction = 1;}
	}
	
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
	
	int speedRatio(double a, int speed){
		if(a == a1){
			 if ( a1 > a3 && a1 > a2 )
		         return speed;
		      else if ( a3 > a1 && a3 > a2 )
		    	  return (int)((double)speed*(double)a1/(double)a3)+10;
		      else if ( a2 > a1 && a2 > a3 )
		    	  return (int)((double)speed*(double)a1/(double)a2)+10;
		      else   
		         return speed;
			}
		if(a == a3){
			 if ( a1 > a3 && a1 > a2 )
				 return (int)((double)speed*(double)a2/(double)a1)+10;
		      else if ( a3 > a1 && a3 > a2 )
		    	  return speed;
		      else if ( a2 > a1 && a2 > a3 )
		    	  return (int)((double)speed*(double)a2/(double)a2)+10;
		      else   
		         return speed;
			}
		if(a == a2){
			 if ( a1 > a3 && a1 > a2 )
				 return (int)((double)speed*(double)a3/(double)a1)+10;
		      else if ( a3 > a1 && a3 > a2 )
		    	  return (int)((double)speed*(double)a3/(double)a3)+10;
		      else if ( a2 > a1 && a2 > a3 )
		    	  return speed;
		      else   
		         return speed;
			}
		return speed;
	}
	
	
}
