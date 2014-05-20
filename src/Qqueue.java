import java.util.ArrayList;

public class Qqueue {
	ArrayList<Pare> q;
	public Qqueue() {
		super();
		q = new ArrayList<Pare>();
	}

	boolean taken = false;

	synchronized Pare get() {
		if(taken || getSzie()<1){
			System.out.println("Ok, i'll wait...");
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		taken = true;
		System.out.println("Got: " + ((Pare)q.get(0)).a+"|"+((Pare)q.get(0)).b);
		
		Pare pareToProcess = q.get(0);
		q.remove(0);
		taken = false;
		notify();
		return pareToProcess;
	}

	synchronized int getSzie() {
		if(taken){
			System.out.println("Ok, i'll wait...");
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		taken = true;
		System.out.println("Qq size: " + q.size());
		
		
		taken = false;
		notify();
		return q.size();
	}

	
	synchronized void put(Pare p) {
		if(taken){
			System.out.println("Ok, i'll wait...");
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		taken = true;
		q.add(p);
		
		System.out.println("Put: " + p.a+"|"+p.b);
		taken = false;
		notify();
	}
}
