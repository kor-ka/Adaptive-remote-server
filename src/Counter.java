import java.util.Queue;

public class Counter extends Thread {

	private Queue<Pare> q;
	private Results r;

	public Counter(Queue<Pare> q, Results r) {
		this.q = q;
		this.r = r;
	}

	public void run() {
		while (true) {
			synchronized (q) {
				Pare p = q.poll();

				if (p != null) {
					int a = p.getA();
					int b = p.getB();
					if (b < a) {
						int c = a;
						a = b;
						b = c;
					}
					double step = 0.001;

					double countPoint = a;
					double result = 0;
					while (countPoint < b) {
						result += (countPoint + 1) * step;
						countPoint += step;
					}
					System.out.println("Result:" + result);
					r.put("a=" + a + "\n" + "b=" + b + "\n" + result);
				}
			}

		}
	}

}
