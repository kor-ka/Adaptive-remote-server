import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

public class Counter extends Thread{
	
	Qqueue q;
	Results r;
	public Counter(Qqueue q, Results r){
		this.q=q;
		this.r=r;
	}
	
	public void run() {
		while (true){
			Pare p = q.get();
			int a = p.a;
			int b = p.b;
			
			
			//Get current mouse location
			PointerInfo poInfo = MouseInfo.getPointerInfo();
			Point poLoc = poInfo.getLocation();
			int currentx = (int) poLoc.getX();
			int currenty = (int) poLoc.getY();
			
			
			int moveToX=currentx+a;
			int moveToY=currenty+b;
			
			 Robot robot;
			try {
				robot = new Robot();
				 // SET THE MOUSE X Y POSITION
				//robot.mouseMove(a, b);
	            robot.mouseMove(moveToX, moveToY);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
	           
			
		}
	}
}
