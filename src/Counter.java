
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
			if (b<a){
				int c=a;
				a=b;
				b=c;		
			}
			double step  = 0.001;
			
			double stop=a;
			double result = 0;
			while(stop<b){
				result = result+(stop+1)*step;
				stop= stop+step;
			}
			System.out.println("Result:"+result);
			r.put(result);
			
		}
	}
}
