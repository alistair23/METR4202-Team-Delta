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
		ac.toBox(2);
	}
	
	public void doit(int x, int box){
		get();
		toBox(box);
		goHome();
	}

	public void goHome(){
		da.setXY(Home, botHeight, speed);
		while(da.ds.readMoving()){}
		atHome = true;
	}
	
	public void goTo(int x){
		da.setXY(x, botHeight, speed);
		atHome = false;
	}
	
	public void get(){
		da.setXY(da.x, tableHeight, speed);
		while(da.ds.readMoving()){}
		da.setXY(da.x, botHeight, speed);
		while(da.ds.readMoving()){}
	}
	public void put(){
		atHome = false;
		da.setXY(da.x, tableHeight - 50, speed);
		DynamixelSerial.halt(20);
		da.ds.motor(3, (int) (da.a3+45));
		DynamixelSerial.halt(20);
		goHome();
	}
	
	public void toBox(int num){
		atHome = false;
		if(num==1){
			da.setXY(Box1, botHeight, speed);
		}else if(num==2){
			da.setXY(Box2, botHeight, speed);
		}else if(num==3){
			da.setXY(Box3, botHeight, speed);
		}else if(num==4){
			da.setXY(Box4, botHeight, speed);
		}
		while(da.ds.readMoving()){}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
