import java.util.ArrayList;

public class Results {
	ArrayList<Double> r;
	public Results() {
		super();
		r = new ArrayList<Double>();
	}

	boolean taken = false;

	synchronized String get() {
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
		String toSend="";
		for(Double result:r){
			toSend=toSend+":"+result;
		}
		taken = false;
		notify();
		return toSend;
	}

	
	
	synchronized void put(Double  d) {
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
		r.add(d);
		
		taken = false;
		notify();
	}
}
