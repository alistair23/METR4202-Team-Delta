package actuation;

import communication.DynamixelSerial;

public class DynamixelControl {

	double L1 = 144; //length of member 1 in mm
	double L2 = 83; //length of member 2 in mm
	double L3 = 0; //length of member 3 in mm

	//Serial comm port for USB2Dynamixel
	static int port  = 3;
	
	int motor1ID = 1;
	int motor2ID = 2;
	int motor3ID = 3;
	
	double cx,cy,ca1,ca2,ca3;
	double cx1,cx2,cy1,cy2,dx1,dx2,dy1,dy2;
	double dx,dy,da1,da2,da3;
	
	double gradient, yint;
	
	double direction = 1; //direction = -1 for other side of arm. direction = 1 for turntable side
	
	boolean moving = false;
	
	static DynamixelSerial ds = new DynamixelSerial(port);
	
	public static void main(String[] args) {
		
		//Thread t = new Thread(ds);
		//t.start();
		
		DynamixelControl dc = new DynamixelControl();
		
		//ds.motor(1, 45, 100);
		//ds.motor(2, 45, 100);
		//ds.motor(3, 45, 100);
		//dc.calcCXY();
		//dc.dx = 100;
		//dc.dy = 100;
		
		//dc.calcLine();
		
		dc.gotoXY(100, 100);
		dc.gotoXY(150, 150);
	}

	public void calcCXY(){
		//ca1 = ds.readPos("01");
		ca1 = ds.readPos("01");
		
		ca2 = ds.readPos("02");
		
		ca1 = Math.toRadians(ca1);
		ca2 = Math.toRadians(ca2);
		
		cx1 = L1*Math.sin(ca1);
		cx2 = L2*Math.sin(ca1+ca2);
		cx = Math.abs(cx1)+Math.abs(cx2);

		cy1 = L1*Math.cos(ca1);
		cy2 = L2*Math.cos(ca1+ca2);
		//if(cy2<cy1){cy2=-cy2;}
		cy = cy1+cy2;
		//cy=cy-L3;
		
		ca3 = Math.toRadians(180.0)- ca1 - ca2;
		
		ca1 = direction*Math.toDegrees(ca1);
		ca2 = direction*Math.toDegrees(ca2);
		ca3 = direction*Math.toDegrees(ca3);
		
		System.out.println("CPos: "+cx+" , "+cy);
		System.out.println("CAngles: "+ca1+" , "+ca2+" , "+ca3);
	}

	public void calcDA(double newX){
		double newY = gradient*newX + yint;
		
		calcDAng(newX, newY);
		
		
		
		
	}
	
	public void moveXY(int speed){
		ds.motor(motor1ID, (int)da1, speed);
		ds.motor(motor2ID, (int)da2, speed);
		ds.motor(motor3ID, (int)da3, speed);
	}
	
	public void calcCAng(){

		double d = Math.sqrt((cx*cx)+(cy*cy));	
		//d= 100;
		ca1 = Math.toRadians(90.0) - Math.acos(((L1*L1)+(d*d)-(L2*L2))/(2*L1*d)) - Math.acos(cx/d);
		ca2 = Math.toRadians(180.0) - Math.acos(((L1*L1)+(L2*L2)-(d*d))/(2*L1*L2));
		ca3 = Math.toRadians(180.0) - ca1 - ca2;
		
		ca1 = Math.toDegrees(ca1);
		ca2 = Math.toDegrees(ca2);
		ca3 = Math.toDegrees(ca3);
		
		ca1 = direction*ca1;
		ca2 = direction*ca2;
		ca3 = direction*ca3;
		
	}

	public void calcDAng(double x, double y){

		double d = Math.sqrt((x*x)+(y*y));	
		//d= 100;
		da1 = Math.toRadians(90.0) - Math.acos(((L1*L1)+(d*d)-(L2*L2))/(2*L1*d)) - Math.acos(x/d);
		da2 = Math.toRadians(180.0) - Math.acos(((L1*L1)+(L2*L2)-(d*d))/(2*L1*L2));
		da3 = Math.toRadians(180.0) - da1 - da2;
		
		da1 = Math.toDegrees(da1);
		da2 = Math.toDegrees(da2);
		da3 = Math.toDegrees(da3);
		
		da1 = direction*da1;
		da2 = direction*da2;
		da3 = direction*da3;
		
		System.out.println("DPos: "+dx+" , "+dy);
		System.out.println("Pos: "+x+" , "+y);
		System.out.println("DAngles: "+da1+" , "+da2+" , "+da3);
	
	}

	public void calcLine(){
		gradient = (dy-cy)/(dx-cx);
		yint = cy - gradient*cx;
		System.out.println("y="+gradient+" * x + "+yint);
	}
	
	
	
	public void gotoXY(double x, double y){
		while(moving){System.out.println("not doing anything");}
		this.dx=x;
		this.dy=y;
		try {
			calcCXY(); //calculate current position
		} catch (StringIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calcLine();
		//while not at the destination
		boolean forward;
		double des;
		if (cx<dx){
			forward = true;
			des = dx-cx;
		}else{
			forward = false;
			des = cx-dx;
		}
		while(des > 10){
			if (cx<dx){
				forward = true;
				des = dx-cx;
			}else{
				forward = false;
				des = cx-dx;
			}
			//System.out.println("not at destination : "+Math.abs(cx)+" "+Math.abs(cy));
			//moving = true;
			
			try {
				calcCXY(); //calculate current position
			} catch (StringIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//go to  next x step
			//System.out.println("current( "+cx+" , "+cy+" ) "+"destination( "+dx+" , "+dy+" ) ");
			
				//System.out.println("Moving +ve x");
			if(forward){
				calcDA(cx+5);
			}else{
				calcDA(cx-5);
			}
			 //calculate line to destination
			
			moveXY(900);
			
			while(ds.readMoving()){System.out.println("Waiting to finish moving");}
			System.out.println("val : "+(Math.abs(dx) - Math.abs(cx)));
		}
	
		
	}
	
}
