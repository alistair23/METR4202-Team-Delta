package actuation;

import gui.Window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JTextArea;

import communication.DynamixelSerial;
import communication.Serial;

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
	/**
	public static void main(String[] args) {
		final DynamixelArm da = new DynamixelArm();

		//da.flip(true);
		//da.ds.read("01", "1E");
		//da.gotoXY(100, 100, 1000);
		//da.gotoXY(100, 100, 1000);
		
		da.ds.motor(1, 0, 100);
		da.ds.motor(2, 90, 100);
		da.ds.motor(3, 90, 100);

		Window w = new Window(new Dimension(400,400));
		
		final JButton capc = new JButton("centre");
		capc.setMinimumSize(new Dimension(200,30));
		w.add(capc,0,0,3,1,1,1);
		capc.setBackground(Color.GREEN.darker());
		
		final JButton Load = new JButton("go");
		Load.setMinimumSize(new Dimension(200,30));
		w.add(Load,0,1,3,1,1,1);
		Load.setBackground(Color.GREEN.darker());
		
		JButton save = new JButton("flip");
		save.setMinimumSize(new Dimension(200,30));
		w.add(save,0,2,3,1,1,1);
		save.setBackground(Color.GREEN.darker());
		
		JButton xup = new JButton("X up");
		xup.setMinimumSize(new Dimension(100,30));
		w.add(xup,0,3,1,1,1,1);
		xup.setBackground(Color.RED.darker());
		
		JButton xdown = new JButton("X Down");
		xdown.setMinimumSize(new Dimension(100,30));
		w.add(xdown,2,3,1,1,1,1);
		xdown.setBackground(Color.RED.darker());
		
		JButton yup = new JButton("Y up");
		yup.setMinimumSize(new Dimension(100,30));
		w.add(yup,0,4,1,1,1,1);
		yup.setBackground(Color.RED.darker());
		
		JButton ydown = new JButton("Y Down");
		ydown.setMinimumSize(new Dimension(100,30));
		w.add(ydown,2,4,1,1,1,1);
		ydown.setBackground(Color.RED.darker());
		
		final JTextArea x = new JTextArea("150");
		x.setMinimumSize(new Dimension(200,30));
		w.add(x,0,5,1,1,1,0);

		final JTextArea y = new JTextArea("150");
		y.setMinimumSize(new Dimension(200,30));
		w.add(y,1,5,1,1,1,0);
		
		final JTextArea speed = new JTextArea("150");
		speed.setMinimumSize(new Dimension(200,30));
		w.add(speed,2,5,1,1,1,0);

		w.revalidate();

		
		capc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	da.ds.centermotors();
            }});
		
		Load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	da.setXY(Integer.valueOf(x.getText()), Integer.valueOf(y.getText()), Integer.valueOf(speed.getText()));
            }});
		
		save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	if(da.direction > 0){da.flip(true);}else{da.flip(false);}
            	Load.doClick();
            }});
		xup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	x.setText(String.valueOf(Integer.valueOf(x.getText())+1));
            	da.setXY(da.x, da.y, Integer.valueOf(speed.getText()));
            	Load.doClick();
            }});
		xdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	x.setText(String.valueOf(Integer.valueOf(x.getText())-1));
            	da.setXY(da.x, da.y, Integer.valueOf(speed.getText()));
            	Load.doClick();
            }});
		yup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	y.setText(String.valueOf(Integer.valueOf(y.getText())+1));
            	da.setXY(da.x, da.y, Integer.valueOf(speed.getText()));
            	Load.doClick();
           }});
		ydown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	y.setText(String.valueOf(Integer.valueOf(y.getText())-1));
            	da.setXY(da.x, da.y, Integer.valueOf(speed.getText()));
            	Load.doClick();
            }});

	}**/
	
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
		//d= 100;
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
		//ds.motor(motor1ID, (int)a1, speed);
		//ds.motor(motor2ID, (int)a2, speed);
		//ds.motor(motor3ID, (int)a3, speed);
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
		//ds.motor(motor1ID, (int)a1, speed1);
		//ds.motor(motor2ID, (int)a2, speed2);
		//ds.motor(motor3ID, (int)a3, speed3);
		ds.motorsync((int)a1, speed1, (int)a2, speed2, (int)a3, speed3);
		//for(int i=0;i<100;i++){
		//ds.read("01","24");
		//}
		//ds.read("01","25");
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
		
		//ds.motor(motor1ID, (int)a1, speed1);
		//ds.motor(motor2ID, (int)a2, speed2);
		//ds.motor(motor3ID, (int)a3, speed3);
		//for(int i=0;i<100;i++){
		ds.motorsync((int)a1, speed1, (int)a2, speed2, (int)a3, speed3);
		//ds.read("01","24");
		//}
		//ds.read("01","25");
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
