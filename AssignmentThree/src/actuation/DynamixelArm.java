package actuation;

import communication.DynamixelSerial;

/**
 * @author Ben Rose
 *	
 * Control of the custom Dynamixel robotic arm.
 * Interface should be as in example:
 * 
 * static ArmControl arm = new ArmControl();
 * arm.setThread(radius.intValue(), arm.getBoxNum(value));
 * Thread armThread = new Thread(arm, "armThread");
 * armThread.start();
 *
 */

public class DynamixelArm{

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
	
	public DynamixelArm(){
	}

	public void calcxy(double one, double two){
		a1 = one;
		a2 = two;
		
		x1 = L1*cos(one);
		x2 = L2*cos(one+two);
		this.x = Math.abs(x1)+Math.abs(x2);

		y1 = L1*sin(one);
		y2 = L2*sin(one+two);
		if(y2<y1){y2=-y2;}
		this.y = y1+y2;
		this.y=this.y-L3;
		
		a3 = 180-one - two;
		
		a1 = direction*one;
		a2 = direction*two;
		a3 = direction*a3;
	}
	
	public void calcAng(double x, double y, double a3off){
		this.x = x;
		this.y = y;
		double d = Math.sqrt((x*x)+(y*y));	
		a1 = Math.toRadians(90.0) - Math.acos(((L1*L1)+(d*d)-(L2*L2))/(2*L1*d)) - Math.acos(x/d);
		a2 = Math.toRadians(180.0) - Math.acos(((L1*L1)+(L2*L2)-(d*d))/(2*L1*L2));
		a3 = Math.toRadians(180.0) - a1 - a2;
		
		a1 = Math.toDegrees(a1)-5;
		a2 = Math.toDegrees(a2)+2;
		a3 = Math.toDegrees(a3)-5;
		
		a1 = direction*a1;
		a2 = direction*a2;
		a3 = direction*(a3+a3off);
	}
	
	public void setAng(double ang1, double ang2, int speed){
		calcxy(ang1,ang2);
		ds.motorsync((int)a1, speed, (int)a2, speed, (int)a3, speed);
	}
	
	public void setXY(double x, double y, int speed){
		calcAng(x,y,0);
		System.out.println("Pos: "+this.x+" , "+this.y);
		System.out.println("Ang: "+a1+" , "+a2+" , "+a3);
		int speed1 = (int)speedRatio(a1,speed);
		int speed2 = (int)speedRatio(a2,speed);
		int speed3 = (int)speedRatio(a3,speed);
		System.out.println("Speeds: "+speed1+"  "+speed2+"  "+speed3);
		ds.motorsync((int)a1, speed1, (int)a2, speed2, (int)a3, speed3);
	}
	
	public void setXY(double x, double y, int speed, double ang){
		calcAng(x,y,ang);
		System.out.println("Pos: "+this.x+" , "+this.y);
		System.out.println("Ang: "+a1+" , "+a2+" , "+a3);
		int speed1 = (int)speedRatio(a1,speed);
		int speed2 = (int)speedRatio(a2,speed);
		int speed3 = (int)speedRatio(a3,speed);
		System.out.println("funct 2 Speeds: "+speed1+"  "+speed2+"  "+speed3);
		System.out.println("funct 2 angles: "+(int)a1+"  "+(int)a2+"  "+(int)a3);
		ds.motorsync((int)a1, speed1, (int)a2, speed2, (int)a3, speed3);
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
	
	double speedRatio(double a, double speed){
		
		a = Math.abs(a);
		double an1 = Math.abs(a1);
		double an2 = Math.abs(a2);
		double an3 = Math.abs(a3);
		
		if(a == an1){
			 if ( an1 > an3 && an1 > an2 )
		         return speed;
		      else if ( an2 > an1 && an2 > an3 && an2 != 0 )
		    	  return (int)((double)speed*(double)an1/(double)an2)+40;
		      else if ( an3 > an1 && an3 > an2 && an3 != 0 )
		    	  return (int)((double)speed*(double)an1/(double)an3)+40;
		      else   
		         return speed;
			}
		else if(a == an2){
			 if ( an1 > an3 && an1 > an2 && an1 != 0 )
				 return (int)((double)speed*(double)an2/(double)an1)+40;
		      else if ( an2 > an1 && an2 > an3 )
		    	  return speed;
		      else if ( an3 > an1 && an3 > an2 && an3 != 0 )
		    	  return (int)((double)speed*(double)an2/(double)an3)+40;
		      else   
		         return speed;
			}
		else if(a == an3){
			 if ( an1 > an3 && an1 > an2 && an1 != 0 )
				 return (int)((double)speed*(double)an3/(double)an1)+40;
		      else if ( an2 > an1 && an2 > an3 && an2 != 0 )
		    	  return (int)((double)speed*(double)an3/(double)an2)+40;
		      else if ( an3 > an1 && an3 > an2 )
		    	  return speed;
		      else   
		         return speed;
			}

		return speed;
	}
}
