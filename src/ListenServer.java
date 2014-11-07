import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import sun.awt.shell.ShellFolder;
import util.WindowsShortcut;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;




import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

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

                    replyServerNameAndProcess(out);

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
                    replyServerNameAndProcess(out);
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

                    replyServerNameAndProcess(out);
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

                    replyServerNameAndProcess(out);
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

                    replyServerNameAndProcess(out);
					break;
					
					case protocol.launchFromTaskBarList:

                        if(OSValidator.isWindows()){
                            String userHome = System.getProperty("user.home");
                            File folder = new File(userHome+"/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar");
                            File[] listOfFiles = folder.listFiles();

                            List<WindowInfo> windows = getWindowsProcessesTitles();
                            ArrayList<String> processes = new ArrayList<String>();
                            for (WindowInfo w:windows){
                                processes.add(w.process);
                            }


                            String s= "";
                            for (int i = 0; i < listOfFiles.length; i++) {
                                if (listOfFiles[i].isFile()&& !listOfFiles[i].getName().contains(".ini") && !(listOfFiles[i].getName().contains("(") && listOfFiles[i].getName().contains(")"))) {
                                    if(!processes.contains(new WindowsShortcut(listOfFiles[i]).getRealFilename()))s+=listOfFiles[i].getName().replace(".lnk", "")+"<split1>";
                                    System.out.println("File " + listOfFiles[i].getName());

                                }

                            }
                            for (WindowInfo w:windows){
                                if(w.process!=null&&!w.title.isEmpty()&&!w.title.equals(""))s+=w.process+"<split2>"+w.title+"<split3>"+w.hwnd+"<split1>";
                            }

                            out.writeUTF(s);
                        }

						    out.flush();
						break;
						
					case protocol.getTaskBarIcons:
						
						       
								try {
									
									String userHome2 = System.getProperty("user.home");  
									
									File f = new File(userHome2+"/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar/"+prtcl.output);
									ShellFolder shellFolder;
									if(f.exists()){
										WindowsShortcut lnk = new WindowsShortcut(f);	
										/*
										if(lnk.getRealFilename().length()>1){
											shellFolder = ShellFolder.getShellFolder(new File(lnk.getRealFilename()));
										}else{
											shellFolder = ShellFolder.getShellFolder(f);
										}
										*/
										shellFolder = ShellFolder.getShellFolder(f);
										
									}else{
										String[] exepath = prtcl.output.split("<split1>");
										shellFolder = ShellFolder.getShellFolder(new File(exepath[0].replace(".lnk", "").replace("\\Device\\HarddiskVolume2", "C:").replace("\\Device\\HarddiskVolume3", "D:")));
									}
									
									 Icon icon = new ImageIcon(shellFolder.getIcon(true));  
									 //Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(userHome2+"/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar/"+prtcl.output));
									 BufferedImage bi = new BufferedImage(
											    icon.getIconWidth(),
											    icon.getIconHeight(),
											    BufferedImage.TYPE_INT_ARGB);
											Graphics g = bi.createGraphics();
											// paint the Icon to the BufferedImage.
											icon.paintIcon(null, g, 0,0);
											g.dispose();
											
											ByteArrayOutputStream baos = new ByteArrayOutputStream();
											ImageIO.write(bi, "png", baos);
											baos.toByteArray();

											out.write(baos.toByteArray());
										   
									 
								} catch (IOException e) {
									e.printStackTrace();
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						      
						        
						      
								 out.flush();
						    
						    
						break;
						
					case protocol.setForegroundWindow:
						int hwnd = Integer.parseInt(prtcl.output);
						
						User32.instance.ShowWindow(hwnd, 9);	
						
						User32.instance.SetForegroundWindow(hwnd);
						replyServerNameAndProcess(out);
						
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

                        replyServerNameAndProcess(out);
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
                    replyServerNameAndProcess(out);
					break;
					
				case protocol.commandLine:

                    if(OSValidator.isWindows()){
                        Runtime.getRuntime().exec("cmd /c"+prtcl.outputCommandLine);
                    }else if(OSValidator.isUnix()){
                        Runtime.getRuntime().exec(prtcl.outputCommandLine);
                    }
                    replyServerNameAndProcess(out);

					break;
					
				case protocol.shortcut:
					String[] buttons = prtcl.outputShortcut.split(" \\+ ");
					int[] keyCodes = new int[buttons.length];
					for (int i = 0; i < buttons.length; i++) {
						keyCodes[i]=buttonStringToInt(buttons[i]);
					}
					doType(keyCodes);
                    replyServerNameAndProcess(out);
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
				
			} catch ( ParseException e){
                e.printStackTrace();
            }

		}

	}

    private void replyServerNameAndProcess(final DataOutputStream out) throws IOException {
        final InetAddress localMachine = InetAddress.getLocalHost();


        try {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);

                    out.writeUTF("ok"+localMachine.getHostName()+"<process>"+getActiveWindowProccessName());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        }catch (UnsatisfiedLinkError e){
            e.printStackTrace();
        }catch (NoClassDefFoundError e){
            e.printStackTrace();
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
    
    private int buttonStringToInt(String s){
    	int inttoRetturn =1;
    	if(s.length()==1){
    		return KeyEvent.getExtendedKeyCodeForChar(s.charAt(0));
    	}else{
            if (s.equals("Ctrl")) {
                inttoRetturn = VK_CONTROL;

            } else if (s.equals("Alt")) {
                inttoRetturn = VK_ALT;

            } else if (s.equals("Shift")) {
                inttoRetturn = VK_SHIFT;

            } else if (s.equals("Tab")) {
                inttoRetturn = VK_TAB;

            } else if (s.equals("Win")) {
                if(OSValidator.isWindows()){
                    inttoRetturn = VK_WINDOWS;
                }else if(OSValidator.isUnix()){
                    //Still not workking...
                    inttoRetturn = VK_WINDOWS;
                    //inttoRetturn = 0x020C;
                    //inttoRetturn = 0xff85;
                    //inttoRetturn = 0xff86;
                    //inttoRetturn = 0x00ce;

                    System.out.println(inttoRetturn);
                }


            } else if (s.equals("Del")) {
                inttoRetturn = VK_DELETE;

            } else if (s.equals("Ins")) {
                inttoRetturn = VK_INSERT;

            } else if (s.equals("Home")) {
                inttoRetturn = VK_HOME;

            } else if (s.equals("End")) {
                inttoRetturn = VK_END;

            } else if (s.equals("Page Up")) {
                inttoRetturn = VK_PAGE_UP;

            } else if (s.equals("Page Down")) {
                inttoRetturn = VK_PAGE_DOWN;

            } else if (s.equals("Esc")) {
                inttoRetturn = VK_ESCAPE;

            } else if (s.equals("Enter")) {
                inttoRetturn = VK_ENTER;

            } else if (s.equals("Space")) {
                inttoRetturn = VK_SPACE;

            } else if (s.equals("Backspace")) {
                inttoRetturn = VK_BACK_SPACE;

            } else if (s.equals("+")) {
                inttoRetturn = VK_PLUS;

            } else if (s.equals("-")) {
                inttoRetturn = VK_MINUS;

            } else if (s.equals("Up arrow")) {
                inttoRetturn = VK_UP;

            } else if (s.equals("Down arrow")) {
                inttoRetturn = VK_DOWN;

            } else if (s.equals("Left arrow")) {
                inttoRetturn = VK_LEFT;

            } else if (s.equals("Right arrow")) {
                inttoRetturn = VK_RIGHT;

            } else if (s.equals("F1")) {
                inttoRetturn = VK_F1;

            } else if (s.equals("F2")) {
                inttoRetturn = VK_F2;

            } else if (s.equals("F3")) {
                inttoRetturn = VK_F3;

            } else if (s.equals("F4")) {
                inttoRetturn = VK_F4;

            } else if (s.equals("F5")) {
                inttoRetturn = VK_F5;

            } else if (s.equals("F6")) {
                inttoRetturn = VK_F6;

            } else if (s.equals("F7")) {
                inttoRetturn = VK_F7;

            } else if (s.equals("F8")) {
                inttoRetturn = VK_F8;

            } else if (s.equals("F9")) {
                inttoRetturn = VK_F9;

            } else if (s.equals("F10")) {
                inttoRetturn = VK_F10;

            } else if (s.equals("F11")) {
                inttoRetturn = VK_F11;

            } else if (s.equals("F12")) {
                inttoRetturn = VK_F12;

            }
    	}
    
    	return inttoRetturn;
    }

    //UTIL

    //get win process
    private static final int MAX_TITLE_LENGTH = 1024;

    private String getActiveWindowProccessName(){
        if(OSValidator.isWindows()){
            /*
            char[] buffer = new char[MAX_TITLE_LENGTH * 2];
            User32DLL.GetWindowTextW(User32DLL.GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
            System.out.println("Active window title: " + Native.toString(buffer));


            char[] buffer2 = new char[MAX_TITLE_LENGTH * 2];
            PointerByReference pointer = new PointerByReference();
            User32DLL.GetWindowThreadProcessId(User32DLL.GetForegroundWindow(), pointer);
            Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());

            Psapi.GetModuleBaseNameW(process, null, buffer2, MAX_TITLE_LENGTH);
            System.out.println("Active window process: " + Native.toString(buffer2));
            return Native.toString(buffer2).replace(".exe", "").replace(".EXE", "");
            */
            PsApi psapi = (PsApi) Native.loadLibrary("psapi", PsApi.class);
            HWND focusedWindow = com.sun.jna.platform.win32.User32.INSTANCE.GetForegroundWindow();
            
            byte[] name = new byte[2048];

            IntByReference pid = new IntByReference();
            com.sun.jna.platform.win32.User32.INSTANCE.GetWindowThreadProcessId(focusedWindow, pid);
            
            WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, pid.getValue());
            /*
            psapi.GetModuleFileNameExA(process, null, name, 2048);
            String nameString= Native.toString(name);
            System.out.println(1+nameString);

            psapi.GetModuleFileNameExW(process, null, name, 2048);
            nameString= Native.toString(name);
            System.out.println(2+nameString);


            psapi.GetProcessImageFileNameW(process, name, 2048);
            nameString= Native.toString(name);
            System.out.println(3+nameString);
            */
            psapi.GetProcessImageFileNameA(process, name, 2048);
            String nameString= Native.toString(name);
            System.out.println(nameString);

            return  nameString.substring(nameString.lastIndexOf("\\")+1).replace(".exe", "").replace(".EXE", "");
            //<----------

        }else if(OSValidator.isUnix()){
            try {
                //xprop -id `xprop -root | grep "_NET_ACTIVE_WINDOW(WINDOW)" | awk '{print $5}'` | grep "WM_CLASS(STRING)"
                Process p = Runtime.getRuntime().exec("xdotool getwindowfocus getwindowname");

                InputStream stderr = p.getErrorStream();
                InputStreamReader isr2 = new InputStreamReader(stderr);
                BufferedReader br2 = new BufferedReader(isr2);
                String line2 = null;
                System.out.println("<ERROR>");
                while ( (line2= br2.readLine()) != null)
                    System.out.println(line2);
                System.out.println("</ERROR>");



                String toRet ="";
                InputStream stdin = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(stdin);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                System.out.println("<OUTPUT>");
                while ( (line = br.readLine()) != null){
                    System.out.println(line);
                    toRet+=line;
                }

                System.out.println("</OUTPUT>");
                return (toRet.lastIndexOf(" - ")!=-1)?toRet.substring(toRet.lastIndexOf(" - ")+3):"nautilus";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }else{
            return "";
        }

    }
    
    //WIN STAFF

/*
    static class Psapi {
        static { Native.register("psapi"); }
        public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);

    }
*/

    public List<WindowInfo> getWindowsProcessesTitles(){
    	 final List<WindowInfo> inflList = new ArrayList<WindowInfo>();
    	    final List<Integer> order = new ArrayList<Integer>();
    	    int top = User32.instance.GetTopWindow(0);
    	    while (top!=0) {
    	        order.add(top);
    	        top = User32.instance.GetWindow(top, User32.GW_HWNDNEXT);
    	    }
    	    User32.instance.EnumWindows(new WndEnumProc()
    	    {
    	        public boolean callback(int hWnd, int lParam)
    	        {
    	        if (User32.instance.IsWindowVisible(hWnd)) {
    	            
    	            
    	                byte[] buffer = new byte[1024];
    	                byte[] name = new byte[2048];
    	                
    	                IntByReference pid = new IntByReference();
    	                PsApi psapi = (PsApi) Native.loadLibrary("psapi", PsApi.class);
    	                User32.instance.GetWindowThreadProcessId(hWnd, pid);    	                
    	                WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, pid.getValue());    	                
    	                psapi.GetProcessImageFileNameA(process, name, 2048);
    	                String nameString= Native.toString(name);
    	                
    	                User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
    	                String title = Native.toString(buffer, "CP1251");
    	                inflList.add(new WindowInfo(hWnd, title, nameString));
    	            
    	        }
    	        return true;
    	    }
    	    }, 0);
    	    
    	    for (WindowInfo w : inflList) {
    	    System.out.println(w);
    	    }
    	    
    	   return inflList;
    }
    
    public interface PsApi extends StdCallLibrary {

        int GetModuleFileNameExA(WinNT.HANDLE process, WinNT.HANDLE module ,
                                 byte[] name, int i);
        int GetModuleFileNameExW(WinNT.HANDLE process, WinNT.HANDLE module ,
                                 byte[] name, int i);

        int GetProcessImageFileNameW(WinNT.HANDLE hProcess, byte[] lpImageFileName,
                                    int nSize);
        int GetProcessImageFileNameA(WinNT.HANDLE hProcess, byte[] lpImageFileName,
                                     int nSize);

    }

    public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
        boolean callback (int hWnd, int lParam);
    }

    public static interface User32 extends StdCallLibrary
    {
        final User32 instance = (User32) Native.loadLibrary ("user32", User32.class);
        boolean EnumWindows (WndEnumProc wndenumproc, int lParam);
        boolean ShowWindow(int hwnd, int i);
		boolean SetForegroundWindow(int hwnd);
		void GetWindowThreadProcessId(int hWnd, IntByReference pid);
		boolean IsWindowVisible(int hWnd);
        
        void GetWindowTextA(int hWnd, byte[] buffer, int buflen);
        void GetWindowTextW(int hWnd, byte[] buffer, int buflen);        
        int GetTopWindow(int hWnd);
        int GetWindow(int hWnd, int flag);
        final int GW_HWNDNEXT = 2;
    }

   
    public static class WindowInfo {
        int hwnd;
        
        String title;
        String process;
        public WindowInfo(int hwnd, String title, String process)
        { this.hwnd = hwnd; this.title = title;  this.process=process;}

        public String toString() {
            return title+ "|" + process;
        }
    }
    
   

}
