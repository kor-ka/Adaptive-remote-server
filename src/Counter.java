
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
			
			
			new Thread(new CountThread(p,r)).start();
			
			
			
		}
	}
}
