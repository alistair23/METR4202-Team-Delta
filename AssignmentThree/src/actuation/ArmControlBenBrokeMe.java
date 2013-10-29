package actuation;

import java.awt.Dimension;

import communication.DynamixelSerial;

public class ArmControlBenBrokeMe implements Runnable{
	
	DynamixelArm da = new DynamixelArm();
	
	
	
	private int Home = 190;
	
	int GLOBALOFFSET = -12;
	

	int tableHeight = 67;

	int botHeight = 100;

	int xoffset = 200;
	
	int minX = 30;
	int maxX = 200;

	int minY = 30;
	int maxY = 200;

	int minA = 30;
	int maxA = 200;

	int speed = 150;
	
	int BoxS1 = 85;
	int BoxS2 = -83;
	
	int Box1 = 130+GLOBALOFFSET;
	int Box2 = 70+GLOBALOFFSET;
	int Box3 = -120-GLOBALOFFSET;
	int Box4 = -70-GLOBALOFFSET;
	boolean atHome = false;

	public ArmControlBenBrokeMe(){
		//this.goHome();
		da.port = 3;
	}
	public static void main(String[] args) {
		ArmControlBenBrokeMe ac;
		(new Thread(ac = new ArmControlBenBrokeMe())).start();
		
		
		//ac.goTo(ac.Box1, 150, 0);
		//ac.da.setAng(0, 90, 200);
		//while(ac.da.ds.readMoving()){DynamixelSerial.halt(10);}
		ac.goHome();
		ac.get(200);
		ac.put(1);
		ac.get(170);
		ac.put(1);
		ac.get(170);
		ac.put(2);
		ac.get(170);
		ac.put(3);
		ac.get(170);
		ac.put(4);

		
		
		/**for(int i =160; i<170;i++){
			ac.get(i);

			if(i%2 == 0){ac.put(2);}else{ac.put(1);}
		}**/
		
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
	
	public void goHome(){
		goTo(Home, botHeight);
		atHome = true;
	}
	
	public void goTo(int x, int y){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(OffsetX(Math.abs(x)), OffsetY(y), speed);
		atHome = false;
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	public void goTo(int x, int y, int ang){
		if(x < 0){da.flip(true);}
		else{da.flip(false);}
		da.setXY(OffsetX(Math.abs(x)), OffsetY(y), speed, OffsetA(ang));
		atHome = false;
		while(da.ds.readMoving()){DynamixelSerial.halt(10);}
	}
	
	public void get(int x){
		if(x>180){
			goTo(x-10, tableHeight+15, 0);
			//while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			goTo(x-5, tableHeight+15, 0);
			//while(da.ds.readMoving()){DynamixelSerial.halt(10);}
			goTo(x, tableHeight, speed/2);
			//while(da.ds.readMoving()){DynamixelSerial.halt(10);}
		} else {
			goTo(x, tableHeight+15, speed/2);
			goTo(x, tableHeight+5, speed/2);
		}

		goTo(Home-10, botHeight+35, speed/3);	
	}
	
	public void getRad(int r){
		int x = xoffset - r;
		get(x);
	}
	
	public void put(int num){
		if(num == 1){
			da.setXY(Box1, botHeight+10, 40);
			da.setXY(Box1, botHeight-20, 0);
			da.setXY(Box1+20, botHeight+20, 0);
		}else if(num == 2){
			da.setXY(Box2+60, botHeight+20, 0);
			da.setXY(Box2+60, botHeight-15, 0);
			da.setXY(Box2, botHeight-10, 0);
			da.setXY(Box2, botHeight+40, 0);
		}else if(num == 3){
			da.setXY(Box3, botHeight+10, 40);
			da.setXY(Box3, botHeight-23, 0);
			da.setXY(Box3-20, botHeight+20, 0);
		}else if(num == 4){
			da.setXY(Box4-60, botHeight+20, 0);
			da.setXY(Box4-60, botHeight-15, 0);
			da.setXY(Box4, botHeight-10, 0);
			da.setXY(Box4, botHeight+40, 0);
		}
			
		
		atHome = false;
	
		goHome();
	}
	
	public int OffsetX(int x){
		int offset = 0;
		if(x<minX){
			return minX;
		}else if(x>0 && x<10){
			
		}else if(x>0 && x<10){
			
		}else if(x>maxX){
			return maxX;
		}
		return x+offset;
	}
	
	public int OffsetY(int y){
		int offset = 0;
		if(y<minY){
			return minY;
		}else if(y>0 && y<10){
			
		}else if(y>0 && y<10){
			
		}else if(y>maxY){
			return maxY;
		}
		return y+offset;
	}
	
	public int OffsetA(int a){
		int offset = 0;
		if(a<minA){
			return minA;
		}else if(a>0 && a<10){
			
		}else if(a>0 && a<10){
			
		}else if(a>maxA){
			return maxA;
		}
		return a+offset;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
