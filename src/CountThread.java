
public class CountThread extends Thread{
	Pare p;
	Results r;
	public CountThread(Pare p, Results r){
		this.p=p;
		this.r=r;
	}
	
	public void run(){
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
		r.put("a="+a+"\n"+"b="+b+"\n"+result);
	}
}
