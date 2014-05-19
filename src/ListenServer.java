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

public class ListenServer extends Thread {
	protected ServerSocket listenSocket;
	DataOutputStream out;
	Socket socket;
	ArrayList<String> clientIps;
	public ListenServer(ServerSocket listenSocket) {
		this.listenSocket = listenSocket;
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
				protocol prtcl = new protocol();
				switch (prtcl.processInput(line)) {
				case protocol.ab:
					System.out.println("a:" + prtcl.outputA);
					System.out.println("b:" + prtcl.outputB);
					out.writeUTF("lol, that was ab!");
					out.flush();

					break;

				case protocol.register:
					
					System.out.println("rtying to register:"+prtcl.outputIP+SocketKeeper.port);
		           
					clientIps.add(socket.getInetAddress().getHostAddress());

					out.writeUTF("registred");
					out.flush();

					System.out.println("registred:"+prtcl.outputIP+SocketKeeper.port);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public void say(String say){
		for(String clientIp:clientIps){
			 InputStream sin;
			try {
				InetAddress ipAddress = InetAddress.getByName(clientIp);
				Socket clientSocket = new Socket(ipAddress, SocketKeeper.port);
				sin = clientSocket.getInputStream();
			
	            OutputStream sout = clientSocket.getOutputStream();

	            
	            DataInputStream in = new DataInputStream(sin);
	            DataOutputStream out = new DataOutputStream(sout);

	            
	                out.writeUTF(say); 
	                out.flush(); 
	                clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}