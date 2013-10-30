package actuation;

import java.awt.Dimension;

import communication.DynamixelSerial;

public class ArmControl extends Thread {
	
	DynamixelArm da = new DynamixelArm();
		
	private int Home = 140;
	
	int GLOBALOFFSET = -12;
	
	int tableHeight = 67;

	int botHeight = 100;

	int xoffset = 235;
	
	int speed = 150;
	
	int BoxS1 = 85;
	int BoxS2 = -83;
	
	int Box1 = 130+GLOBALOFFSET;
	int Box2 = 70+GLOBALOFFSET;
	int Box3 = -120-GLOBALOFFSET;
	int Box4 = -70-GLOBALOFFSET;
	boolean atHome = false;
	
	static boolean INIT = true;
	static int BOX;
	static int RADIUS;

	public ArmControl(){
		this.goHome();
		da.port = 3;
	}
	public static void main(String[] args) {
		ArmControl ac;
		
		ac = new ArmControl();
		INIT = false;
		
		ac.goHome();
	//	ac.setThread(95, 2);
	//	ac.run();
	}
	
	public int getBoxNum(double value) {
		if (value == 0.5 || value == 0.2) {
			return 2;
		} else if (value == 0.1 || value == 0.05) {
			return 1;
		} else if (value == 1.0) {
			return 3;
		} else if (value == 2.0) {
			return 4;
		}
		// if not a coin value return boxx #1
		return 1;
	}
	
	public void goHome(){
		da.flip(false);
		da.setXY(Home, botHeight+10, speed);
		da.setXY(Home, botHeight+10, speed);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		atHome = true;
	}
	
	private void goTo(int x, int y){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), y, speed);
		da.setXY(Math.abs(x), y, speed);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	private void goTo(int x, int y, int ang){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), y, speed, ang);
		da.setXY(Math.abs(x), y, speed, ang);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	private void goTo(int x, int y, int ang, int speedy){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), y, speedy, ang);
		da.setXY(Math.abs(x), y, speedy, ang);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	public boolean ready() {
		return (da.ds.readMoving() && atHome);
	}
	
	private void get(int x){
		atHome = false;
		if (x>215) {
			x = 215;
		}
		if (x>200) {
			goTo(x-20, botHeight+25, 0, speed/3);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			goTo(x-15, tableHeight+20, 0, speed/3);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			goTo(x-10, tableHeight+15, 0, speed/3);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			//goTo(x-5, tableHeight+5, 0);
			//while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			da.setXY(x, tableHeight, speed/2);
			da.setXY(x, tableHeight, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		} else if (x > 185) {
			goTo(x-10, tableHeight+15, 0);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			goTo(x-5, tableHeight+15, 0);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			da.setXY(x, tableHeight+5, speed/2);
			da.setXY(x, tableHeight+5, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		} else if(x>180){
			goTo(x-10, tableHeight+15, 0);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			goTo(x-5, tableHeight+15, 0);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			da.setXY(x, tableHeight, speed/2);
			da.setXY(x, tableHeight, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		} else if (x>170){
			da.setXY(x, tableHeight+15, speed/2);
			da.setXY(x, tableHeight+15, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			da.setXY(x, tableHeight+3, speed/2);
			da.setXY(x, tableHeight+3, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		} else {
			da.setXY(x, tableHeight+15, speed/2);
			da.setXY(x, tableHeight+15, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			da.setXY(x, tableHeight+5, speed/2);
			da.setXY(x, tableHeight+5, speed/2);
			while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		}

		da.setXY(Home+30, botHeight+35, speed/3);
		da.setXY(Home+30, botHeight+35, speed/3);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}

	}
	
	private void getCoin(int r){
		int x = xoffset - r;
		get(x);
	}
	
	private void put(int num){
		atHome = false;
		if(num == 1){
			goTo(Box1, botHeight+10, 40);
			goTo(Box1, botHeight-20, 0);
			goTo(Box1+20, botHeight+20, 0);
		}else if(num == 2){
			goTo(Box2+60, botHeight+20, 0);
			goTo(Box2+60, botHeight-15, 0);
			goTo(Box2, botHeight-10, 0);
			goTo(Box2, botHeight+40, 0);
		}else if(num == 3){
			goTo(Box3, botHeight+10, 40);
			goTo(Box3, botHeight-23, 0);
			goTo(Box3-20, botHeight+20, 0);
		}else if(num == 4){
			goTo(Box4-60, botHeight+20, 0);
			goTo(Box4-60, botHeight-15, 0);
			goTo(Box4, botHeight-10, 0);
			goTo(Box4, botHeight+40, 0);
		}
			
		//goTo(num,tableHeight-20);
		//DynamixelSerial.halt(2000);
		
		//while(da.ds.readMoving()){}
		//da.ds.motor(3, 0);
		//goTo(num+50,tableHeight);
		//while(da.ds.readMoving()){}
		goHome();
	}
	
	public void setThread(int radius, int box) {
		this.BOX = box;
		this.RADIUS = radius;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if (INIT) {
			INIT = false;
			return;
		}
		
		atHome = false;
		int x = xoffset - RADIUS;
		get(x);
		if(BOX == 1){
			goTo(Box1, botHeight+10, 40);
			goTo(Box1, botHeight-20, 0);
			goTo(Box1+20, botHeight+20, 0);
		}else if(BOX == 2){
			goTo(Box2+60, botHeight+20, 0);
			goTo(Box2+60, botHeight-15, 0);
			goTo(Box2, botHeight-10, 0);
			goTo(Box2, botHeight+40, 0);
		}else if(BOX == 3){
			goTo(Box3, botHeight+10, 40);
			goTo(Box3, botHeight-23, 0);
			goTo(Box3-20, botHeight+20, 0);
		}else if(BOX == 4){
			goTo(Box4-60, botHeight+20, 0);
			goTo(Box4-60, botHeight-15, 0);
			goTo(Box4, botHeight-10, 0);
			goTo(Box4, botHeight+40, 0);
		}
		goHome();
	}
	
}
