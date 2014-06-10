import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import static java.awt.event.KeyEvent.*;

public class ListenServer extends Thread {
	protected ServerSocket listenSocket;
	DataOutputStream out;
	Socket socket;
	ArrayList<String> clientIps;
	Qqueue q;
	Results r;
	public ListenServer(ServerSocket listenSocket, Qqueue q, Results r) {
		this.listenSocket = listenSocket;
		this.q = q;
		this.r = r;
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
				
				Robot robot;
				StringSelection stringSelection;
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();

				line = in.readUTF();
				System.out.println("Client say: " + line);
				protocol prtcl = new protocol();
				switch (prtcl.processInput(line)) {
				case protocol.ab:
					int a= Integer.parseInt(prtcl.outputA);
					int b= Integer.parseInt(prtcl.outputB);
					q.put(new Pare(a, b));
					System.out.println("a:" + a);
					System.out.println("b:" + b);
					
					out.writeUTF("ok");
					out.flush();

					break;
					
				case protocol.click:
					
					try {
						robot = new Robot();
						 // LEFT CLICK
			            robot.mousePress(InputEvent.BUTTON1_MASK);
			            robot.mouseRelease(InputEvent.BUTTON1_MASK);
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
			           
					break;
					
				case protocol.rclick:
					
					try {
						robot = new Robot();
						 // R CLICK
			            robot.mousePress(InputEvent.BUTTON3_MASK);
			            robot.mouseRelease(InputEvent.BUTTON3_MASK);
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
			           
					break;
					
				case protocol.dndDown:
					
					try {
						robot = new Robot();
						 // LEFT CLICK
			            robot.mousePress(InputEvent.BUTTON1_MASK);
			            
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
			           
					break;
					
					case protocol.dndUp:
					
					try {
						robot = new Robot();
						 // LEFT CLICK
			            
			            robot.mouseRelease(InputEvent.BUTTON1_MASK);
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
			           
					break;
					
					case protocol.launch:
						stringSelection = new StringSelection (prtcl.outputToLounch);
						clpbrd.setContents (stringSelection, null);
						doType(VK_WINDOWS);
					try {
						Thread.sleep(500);
						doType(VK_1);
						Thread.sleep(200);
						doType(VK_BACK_SPACE);
						doType(VK_CONTROL, VK_V);
						doType(VK_ENTER);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
						
						
						break;
					
					case protocol.keyboard:
					try {
						robot = new Robot();
						char chr = prtcl.outputChar.charAt(0);
						boolean isChrEnter = KeyEvent.getExtendedKeyCodeForChar(chr)==VK_ENTER;
						boolean isChrSpace = KeyEvent.getExtendedKeyCodeForChar(chr)==VK_SPACE;
						if(prtcl.outputChar.length()>1){
							isChrSpace=false;
						}
						 if (prtcl.outputChar.equals("bksps")){
							doType(VK_BACK_SPACE);
						}else if (prtcl.outputChar.equals("up")){
							doType(VK_UP);
						}else if (prtcl.outputChar.equals("ctrlz")){
							doType(VK_CONTROL, VK_Z);
						}else if (prtcl.outputChar.equals("ctrly")){
							doType(VK_CONTROL, VK_Y);
						}else if (prtcl.outputChar.equals("ctrlc")){
							doType(VK_CONTROL, VK_C);
						}else if (prtcl.outputChar.equals("ctrlv")){
							doType(VK_CONTROL, VK_V);
						}else if (prtcl.outputChar.equals("ctrla")){
							doType(VK_CONTROL, VK_A);
						}else if (prtcl.outputChar.equals("ctrlx")){
							doType(VK_CONTROL, VK_X);
						}else if (prtcl.outputChar.equals("ctrlp")){
							doType(VK_CONTROL, VK_P);
						}else if (prtcl.outputChar.equals("Caps Lock")){
							doType(VK_CAPS_LOCK);
						}else if (prtcl.outputChar.equals("Num Lock")){
							doType(VK_NUM_LOCK);
						}else if (prtcl.outputChar.equals("Win")){
							doType(VK_WINDOWS);
						}else if (prtcl.outputChar.equals("Del")){
							doType(VK_DELETE);
						}else if (prtcl.outputChar.equals("Insert")){
							doType(VK_INSERT);
						}else if (prtcl.outputChar.equals("Home")){
							doType(VK_HOME);
						}else if (prtcl.outputChar.equals("End")){
							doType(VK_END);
						}else if (prtcl.outputChar.equals("Page Up")){
							doType(VK_PAGE_UP);
						}else if (prtcl.outputChar.equals("Page Down")){
							doType(VK_PAGE_DOWN);
						}else if (prtcl.outputChar.equals("Ctrl+S")){
							doType(VK_CONTROL, VK_S);
						}else if (prtcl.outputChar.equals("Ctrl+Alt+Del")){
							doType(VK_CONTROL, VK_SHIFT, VK_ESCAPE);
						}else if (prtcl.outputChar.equals("Alt+Enter")){
							doType(VK_ALT, VK_ENTER);
						}else if (prtcl.outputChar.equals("Alt+Tab")){
							//OMG Thats HACK!
							Runtime.getRuntime().exec("cmd /c"+"%windir%\\explorer.exe shell:::{3080F90E-D7AD-11D9-BD98-0000947B0257}");
						}else if (prtcl.outputChar.equals("ctrl_shift_z")){
							doType(VK_CONTROL, VK_SHIFT, VK_Z);
						}else if (prtcl.outputChar.equals("f1")){
							doType(VK_F1);
						}else if (prtcl.outputChar.equals("f2")){
							doType(VK_F2);
						}else if (prtcl.outputChar.equals("f3")){
							doType(VK_F3);
						}else if (prtcl.outputChar.equals("f4")){
							doType(VK_F5);
						}else if (prtcl.outputChar.equals("f5")){
							doType(VK_F5);
						}else if (prtcl.outputChar.equals("f6")){
							doType(VK_F6);
						}else if (prtcl.outputChar.equals("f7")){
							doType(VK_F7);
						}else if (prtcl.outputChar.equals("f8")){
							doType(VK_F8);
						}else if (prtcl.outputChar.equals("f9")){
							doType(VK_F9);
						}else if (prtcl.outputChar.equals("f10")){
							doType(VK_F10);
						}else if (prtcl.outputChar.equals("f11")){
							doType(VK_F11);
						}else if (prtcl.outputChar.equals("f12")){
							doType(VK_F12);
						}else if (prtcl.outputChar.equals("down")){
							doType(VK_DOWN);
						}else if (prtcl.outputChar.equals("left")){
							doType(VK_LEFT);
						}else if (prtcl.outputChar.equals("right")){
							doType(VK_RIGHT);
						}else if (prtcl.outputChar.equals("esc")){
							doType(VK_ESCAPE);
						}else if (prtcl.outputChar.equals("enter")){
							doType(VK_ENTER);
						}else if (prtcl.outputChar.equals("contextMenu")){
							doType(VK_CONTEXT_MENU);
						} else if(isChrEnter){
							doType(VK_ENTER);							
						}else if(isChrSpace){
							doType(VK_SPACE);							
						}else{
							stringSelection = new StringSelection (prtcl.outputChar);							
							clpbrd.setContents (stringSelection, null);
							
							doType(VK_CONTROL, VK_V);
						}
						
						
						//robot.keyPress(Character.toUpperCase(prtcl.outputChar.charAt(0)));
					} catch (AWTException e) {
						
						e.printStackTrace();
					}
						
					break;

				case protocol.register:
					
						String ip = socket.getInetAddress().getHostAddress();
						if(!clientIps.contains(ip+":"+prtcl.outputPort)){
							System.out.println("rtying to register:"+ip+":"+prtcl.outputPort);
					           
							clientIps.add(ip+":"+prtcl.outputPort);

							out.writeUTF("registred");
							out.flush();

								System.out.println("registred:"+ip+":"+prtcl.outputPort);
						}else{
							clientIps.remove(ip+":"+prtcl.outputPort);
							out.writeUTF("unregistred");
							out.flush();

								System.out.println("unregistred:"+ip+":"+prtcl.outputPort);
						}
						
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}

		}

	}
	
	public void say(String say){
		for(String clientIpPort:clientIps){
			 InputStream sin;
			 String ip=null;
				int port =-1;
				int delimetr;
			try {
				
				
				delimetr=clientIpPort.indexOf(":");
				ip = clientIpPort.substring(0, delimetr);
				port =Integer.parseInt( clientIpPort.substring(delimetr+1));
				
				
				System.out.println("trying say:"+ip+":"+port);
				
				InetAddress ipAddress = InetAddress.getByName(ip);
				Socket clientSocket = new Socket(ipAddress, port);
				sin = clientSocket.getInputStream();
			
	            OutputStream sout = clientSocket.getOutputStream();

	            
	            DataInputStream in = new DataInputStream(sin);
	            DataOutputStream out = new DataOutputStream(sout);

	            say =r.get();
	                out.writeUTF("results:"+say); 
	                out.flush(); 
	                clientSocket.close();
			} catch (IOException e) {
				clientIps.remove(ip+":"+port);
				
				System.out.println("Ooops, that was exeption, i'l better remove it:"+ip+":"+port);
				e.printStackTrace();
			}
		}
	}

	//Util
	private void doType(int... keyCodes) {
		try{
			doType(keyCodes, 0, keyCodes.length);	
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
        
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }
        Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(keyCodes[offset]);
	        doType(keyCodes, offset + 1, length - 1);
	        robot.keyRelease(keyCodes[offset]);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
