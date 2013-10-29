package actuation;

import java.awt.Dimension;

import communication.DynamixelSerial;

public class ArmControl implements Runnable{
	
	DynamixelArm da = new DynamixelArm();
	
	
	
	private int Home = 190;
	
	int GLOBALOFFSET = -12;
	
	int tableHeight = 70;
	int botHeight = 100;
	int xoffset = 180;
	
	int speed = 200;
	
	int BoxS1 = 85;
	int BoxS2 = -83;
	
	int Box1 = 130+GLOBALOFFSET;
	int Box2 = 70+GLOBALOFFSET;
	int Box3 = -70+GLOBALOFFSET;
	int Box4 = -150+GLOBALOFFSET;
	boolean atHome = false;

	public ArmControl(){
		//this.goHome();
		da.port = 3;
	}
	public static void main(String[] args) {
		ArmControl ac;
		(new Thread(ac = new ArmControl())).start();
		
		
		//ac.goTo(ac.Box1, 150, 0);
		ac.da.setAng(0, 90, 200);
		while(ac.da.ds.readMoving()){DynamixelSerial.halt(10);}
		ac.goHome();
		ac.get(180);
		
		
		for(int i =160; i<170;i++){
			ac.get(i);

			if(i%2 == 0){ac.put(2);}else{ac.put(1);}
		}
		
		System.out.println("Done");
		
		
		//ac.goTo(ac.Box2-5, ac.botHeight-20, 0);
		//ac.goTo(ac.Box2-5, ac.botHeight+20, 0);
		
		//ac.goTo((int)(ac.da.L2), (int)(ac.da.L1), 0);
		//ac.goTo(ac.Box2, ac.botHeight-20, -40);

		
		
		
		
		//ac.goTo(ac.BoxS1, ac.botHeight, 0);
		//ac.goTo(ac.BoxS1, ac.botHeight-25, -20);
		//ac.goTo(ac.BoxS1, ac.botHeight, 0);

		//ac.goTo(ac.BoxS1, ac.botHeight-25, 20);
		//ac.put(1);
		//ac.goTo(130, 150, 0);
		//ac.goTo(110, 150, 0);
		//ac.goTo(90, 150, 0);
		//ac.goTo(90, 130, 0);
		//ac.goTo(90, 110, 0);
		//ac.goTo(90, 90, 0);
		//ac.goTo(90, 70, 0);
	}
	
	public void doit(int x, int box){
		get();
		toBox(box);
		goHome();
	}

	public void goHome(){
		da.flip(false);
		da.setXY(Home, botHeight, speed);
		da.setXY(Home, botHeight, speed);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		atHome = true;
	}
	
	public void goTo(int x){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), botHeight, speed);
		da.setXY(Math.abs(x), botHeight, speed);
		atHome = false;
	}
	
	public void goTo(int x, int y){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), y, speed);
		da.setXY(Math.abs(x), y, speed);
		atHome = false;
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	public void goToR(int x, int y){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(xoffset-x), y, speed);
		da.setXY(Math.abs(xoffset-x), y, speed);
		atHome = false;
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	public void goTo(int x, int y, int ang){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(Math.abs(x), y, speed, ang);
		da.setXY(Math.abs(x), y, speed, ang);
		atHome = false;
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	public void get(){
		da.setXY(da.x, tableHeight+5, speed);
		da.setXY(da.x, tableHeight+5, speed);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		da.setXY(da.x-10, tableHeight+50, speed/2);
		da.setXY(da.x-10, tableHeight+50, speed/2);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	public void get(int x){
		int yoff = 0;
		if(x>170){yoff = 10;}
		goTo(x, botHeight-yoff, 0);
		da.setXY(da.x, tableHeight+5, speed);
		da.setXY(da.x, tableHeight+5, speed/2);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		da.setXY(da.x-10, tableHeight+50-yoff, speed/2);
		da.setXY(da.x-10, tableHeight+50-yoff, speed/2);
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}

	}
	public void put(int num){
		if(num == 1){
			goTo(Box1, botHeight, 40);
			goTo(Box1, botHeight-20, 0);
			goTo(Box1+20, botHeight+20, 0);
		}else if(num == 2){
			goTo(Box2+60, botHeight+20, 0);
			goTo(Box2+60, botHeight-15, 0);
			goTo(Box2, botHeight-10, 0);
			goTo(Box2, botHeight+40, 0);
		}else if(num == 3){
			goTo(BoxS2, botHeight-20, -20);
			goTo(BoxS2, botHeight-20, 20);
		}else if(num == 4){
			goTo(BoxS2, botHeight-20, 20);
			goTo(BoxS2, botHeight-20, -20);
		}
			
		
		atHome = false;
		//goTo(num,tableHeight-20);
		//DynamixelSerial.halt(2000);
		
		//while(da.ds.readMoving()){}
		//da.ds.motor(3, 0);
		//goTo(num+50,tableHeight);
		//while(da.ds.readMoving()){}
		
		goHome();
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
