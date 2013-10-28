package actuation;

import java.awt.Dimension;

import communication.DynamixelSerial;

public class ArmControl implements Runnable{
	
	DynamixelArm da = new DynamixelArm();
	
	private int Home = 160;
	
	int tableHeight = 100;
	int botHeight = 150;
	int xoffset = 180;
	
	int speed = 200;

	int Box1 = 100;
	int Box2 = 70;
	int Box3 = -70;
	int Box4 = -100;
	boolean atHome = false;

	public ArmControl(){
		da.port = 3;
		
		this.goHome();
		
	}
	public static void main(String[] args) {
		ArmControl ac = new ArmControl();
		ac.goHome();
		ac.get();
		ac.toBox(1);
		ac.put(ac.Box1);
		//ac.toBox(2);
		//ac.put(ac.Box2);
		ac.goHome();

	}
	
	public void doit(int x, int box){
		get();
		toBox(box);
		goHome();
	}

	public void goHome(){
		da.flip(false);
		da.setXY(Home, botHeight, speed);
		while(da.ds.readMoving()){}
		atHome = true;
	}
	
	public void goTo(int x){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), botHeight, speed);
		atHome = false;
	}
	
	public void goTo(int x, int y){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), y, speed);
		atHome = false;
	}
	
	public void get(){
		da.setXY(da.x, tableHeight, speed);
		while(da.ds.readMoving()){}
		da.setXY(da.x, botHeight, speed);
		while(da.ds.readMoving()){}
	}
	public void put(int num){
		atHome = false;
		goTo(num,tableHeight-20);
		//DynamixelSerial.halt(2000);
		
		while(da.ds.readMoving()){}
		//da.ds.motor(3, 0);
		goTo(num+50,tableHeight);
		while(da.ds.readMoving()){}
		
		//goHome();
	}
	
	public void toBox(int num){
		atHome = false;
		if(num==1){
			goTo(Box1);
		}else if(num==2){
			goTo(Box2);
		}else if(num==3){
			goTo(Box3);
		}else if(num==4){
			goTo(Box4);
		}
		while(da.ds.readMoving()){}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
