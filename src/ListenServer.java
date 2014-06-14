import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;

public class ListenServer extends Thread {
	protected ServerSocket listenSocket;
	DataOutputStream out;
	Socket socket;
	ArrayList<String> clientIps;
	Queue<Pare> mQueue;
	Results mResults;

	public ListenServer(ServerSocket listenSocket, Queue<Pare> q, Results r) {
		this.listenSocket = listenSocket;
		this.mQueue = q;
		this.mResults = r;
	}

	public void run() {
		clientIps = new ArrayList<String>();
		while (true) {
			try {

				System.out
						.println("Waiting for a client... or you can send thmth");

				socket = listenSocket.accept();

				System.out
						.println("Got a client :) ... Finally, someone saw me through all the cover!");
				System.out.println();

				InputStream sin = socket.getInputStream();
				OutputStream sout = socket.getOutputStream();

				DataInputStream in = new DataInputStream(sin);
				DataOutputStream out = new DataOutputStream(sout);

				String line = null;

				line = in.readUTF();
				System.out.println("Client say: " + line);
				Protocol prtcl = new Protocol();
				switch (prtcl.processInput(line)) {

				case Protocol.AB:
					int a = Integer.parseInt(prtcl.outputA);
					int b = Integer.parseInt(prtcl.outputB);
					synchronized (mQueue) {
						mQueue.add(new Pare(a, b));
					}
					
					System.out.println("a:" + a);
					System.out.println("b:" + b);

					break;

				case Protocol.REGISTER:

					String ip = socket.getInetAddress().getHostAddress();
					if (!clientIps.contains(ip + ":" + prtcl.outputPort)) {
						System.out.println("rtying to register:" + ip + ":"
								+ prtcl.outputPort);

						clientIps.add(ip + ":" + prtcl.outputPort);

						out.writeUTF("registred");
						out.flush();

						System.out.println("registred:" + ip + ":"
								+ prtcl.outputPort);
					} else {
						clientIps.remove(ip + ":" + prtcl.outputPort);
						out.writeUTF("unregistred");
						out.flush();

						System.out.println("unregistred:" + ip + ":"
								+ prtcl.outputPort);
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	
	public void say(String say) {
		for (String clientIpPort : clientIps) {
			
			String ip = null;
			int port = -1;
			
			try {

				String clientIpAndPort[] = clientIpPort.split(":");
				ip = clientIpAndPort[0];
				port = Integer.parseInt(clientIpAndPort[1]);

				System.out.println("trying say:" + ip + ":" + port);

				InetAddress ipAddress = InetAddress.getByName(ip);
				Socket clientSocket = new Socket(ipAddress, port);
				

				OutputStream sout = clientSocket.getOutputStream();

				
				DataOutputStream out = new DataOutputStream(sout);

				say = mResults.get();
				out.writeUTF("results:" + say);
				out.flush();
				clientSocket.close();
			} catch (IOException e) {
				clientIps.remove(ip + ":" + port);

				System.out
						.println("Ooops, that was exeption, i'l better remove it:"
								+ ip + ":" + port);
				e.printStackTrace();
			}
		}
	}

}
