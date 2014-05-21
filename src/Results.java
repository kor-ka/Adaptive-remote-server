import java.util.ArrayList;

public class Results {
	ArrayList<String> r;
	public Results() {
		super();
		r = new ArrayList<String>();
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
		for(String result:r){
			toSend=toSend+":"+result;
		}
		taken = false;
		notify();
		return toSend;
	}

	
	
	synchronized void put(String  s) {
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
		r.add(s);
		
		taken = false;
		notify();
	}
}
