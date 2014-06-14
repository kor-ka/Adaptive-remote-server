import java.net.*;
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

public class SocketKeeper {
	static int port;

	public static void main(String[] ar) {
		//Ask 4 port
		System.out.println("Gimme a POOORT!");
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(
				System.in));
		String portLine = null;
		//Check is port Ok
		try {
			portLine = keyboard.readLine();
			port = Integer.parseInt(portLine);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (port <= 1024 && port >= 65535) {
			System.out.println("R u kidding me? ");
			System.out
					.println("Try again (by the way, the port number must be greater than 1024)");

			try {
				portLine = keyboard.readLine();
				port = Integer.parseInt(portLine);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Port is ok, make ServerSocket
		ServerSocket mServerSocket;
		try {
			mServerSocket = new ServerSocket(port);
			Queue<Pare> mQueue = new LinkedList<Pare>();
			Results mResults = new Results();
			//Start listener & counter
			final Counter cntr = new Counter(mQueue, mResults);
			new Thread(cntr).start();

			final ListenServer ls = new ListenServer(mServerSocket, mQueue, mResults);
			new Thread(ls).start();
			//wait 4 input to send results 2 clients
			while (true) {
				final String say = keyboard.readLine();
				if (say != null) {
						new Thread(new Runnable() {
						public void run() {
							ls.say(say);
						}
					}).start();
				}

			}
		} catch (Exception e) {			
			e.printStackTrace();
		}

		

	}
}
