import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import sun.awt.shell.ShellFolder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.event.KeyEvent.*;

public class ListenServer extends Thread {
	protected ServerSocket listenSocket;
	DataOutputStream out;
	Socket socket;
	ArrayList<String> clientIps;
	Qqueue q;
	Results r;
	JDialog contextBtnOverlay;
	JLabel labelSqare;
	String[] actions;

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
						.println("Waiting for a client... ");

				socket = listenSocket.accept();

				InputStream sin = socket.getInputStream();
				OutputStream sout = socket.getOutputStream();

				DataInputStream in = new DataInputStream(sin);
				DataOutputStream out = new DataOutputStream(sout);

				String line;

				Robot robot;
				StringSelection stringSelection;
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();

				line = in.readUTF();
				System.out.println("Client: " + line);
				Protocol prtcl = new Protocol();
				switch (prtcl.processInput(line)) {
					case Protocol.ab:
						int a = Integer.parseInt(prtcl.outputA);
						int b = Integer.parseInt(prtcl.outputB);
						q.put(new Pare(a, b));
						System.out.println("a:" + a);
						System.out.println("b:" + b);

						replyServerNameAndProcess(out);

						break;

					case Protocol.click:
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

					case Protocol.centerClick:

						try {
							robot = new Robot();
							// R CLICK
							robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
							robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
						} catch (AWTException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						replyServerNameAndProcess(out);
						break;

					case Protocol.rclick:

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

					case Protocol.dndDown:

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

					case Protocol.dndUp:

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

					case Protocol.launchFromTaskBarList:

						if (OSValidator.isWindows()) {
							String userHome = System.getProperty("user.home");
							File folder = new File(userHome + "/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar");
							File[] listOfFiles = folder.listFiles();

							List<WindowInfo> windows = getWindowsProcessesTitles();
							ArrayList<String> processes = new ArrayList<String>();
							for (WindowInfo w : windows) {
								processes.add(w.process);
							}


							String s = "";
							for (int i = 0; i < listOfFiles.length; i++) {
								if (listOfFiles[i].isFile() && !listOfFiles[i].getName().contains(".ini") && !(listOfFiles[i].getName().contains("(") && listOfFiles[i].getName().contains(")"))) {
									s += listOfFiles[i].getName().replace(".lnk", "") + "<split1>";
									System.out.println("File " + listOfFiles[i].getName());

								}

							}
							for (WindowInfo w : windows) {
								if (w.process != null && !w.title.isEmpty() && !w.title.equals("") && !w.title.equals("Program Manager") && !w.title.equals("ColorUService"))
									s += w.process + "<split2>" + w.title + "<split3>" + w.hwnd + "<split1>";
							}

							out.writeUTF(s);
						}

						out.flush();
						break;

					case Protocol.getTaskBarIcons:


						try {

							String userHome2 = System.getProperty("user.home");

							File f = new File(userHome2 + "/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar/" + prtcl.output);
							ShellFolder shellFolder;
							if (f.exists()) {
								System.out.println("File exist!");

								shellFolder = ShellFolder.getShellFolder(f);
								System.out.println("Shell folder " + shellFolder.exists());
							} else {

								String path0 = prtcl.output.replace(".lnk", "").substring(22);
								String path = path0.substring(prtcl.output.indexOf("\\") + 1);
								String drive = "";
								if (new File("C:" + path).exists()) {
									drive = "C:";
								} else if (new File("D:" + path).exists()) {
									drive = "D:";
								} else if (new File("E:" + path).exists()) {
									drive = "E:";
								} else if (new File("F:" + path).exists()) {
									drive = "F:";
								} else if (new File("G:" + path).exists()) {
									drive = "G:";
								} else if (new File("H:" + path).exists()) {
									drive = "H:";
								}

								shellFolder = ShellFolder.getShellFolder(new File(drive + path));
							}

							Icon icon;
							if (shellFolder.getIcon(true) != null) {
								icon = new ImageIcon(shellFolder.getIcon(true));
							} else {
								icon = FileSystemView.getFileSystemView().getSystemIcon(new File(userHome2 + "/AppData/Roaming/Microsoft/Internet Explorer/Quick Launch/User Pinned/TaskBar/" + prtcl.output));
							}

							BufferedImage bi = new BufferedImage(
									icon.getIconWidth(),
									icon.getIconHeight(),
									BufferedImage.TYPE_INT_ARGB);
							Graphics g = bi.createGraphics();
							// paint the Icon to the BufferedImage.
							icon.paintIcon(null, bi.getGraphics(), 0, 0);
							g.dispose();

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(bi, "png", baos);
							baos.toByteArray();

							out.write(baos.toByteArray());


						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						out.flush();


						break;

					case Protocol.setForegroundWindow:
						int hwnd = Integer.parseInt(prtcl.output);

						User32.instance.LockSetForegroundWindow(2);

						if (User32.instance.IsIconic(hwnd)) {
							User32.instance.ShowWindow(hwnd, 9);
						}


						User32.instance.SetForegroundWindow(hwnd);
						replyServerNameAndProcess(out);

						break;

					case Protocol.launch:
						stringSelection = new StringSelection(prtcl.outputToLounch);
						clpbrd.setContents(stringSelection, null);
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

					case Protocol.wheelDown:
						try {
							robot = new Robot();
							robot.mouseWheel(1);
						} catch (AWTException e) {
							e.printStackTrace();
						}


						break;

					case Protocol.wheelUp:
						try {
							robot = new Robot();
							robot.mouseWheel(-1);
						} catch (AWTException e) {
							e.printStackTrace();
						}
						break;

					case Protocol.keyboard:
						try {
							robot = new Robot();
							char chr = prtcl.outputChar.charAt(0);
							boolean isChrEnter = KeyEvent.getExtendedKeyCodeForChar(chr) == VK_ENTER;
							boolean isChrSpace = KeyEvent.getExtendedKeyCodeForChar(chr) == VK_SPACE;
							if (prtcl.outputChar.length() > 1) {
								isChrSpace = false;
							}
							if (prtcl.outputChar.equals("bksps")) {
								doType(VK_BACK_SPACE);
							} else if (prtcl.outputChar.equals("up")) {
								doType(VK_UP);
							} else if (prtcl.outputChar.equals("ctrlz")) {
								doType(VK_CONTROL, VK_Z);
							} else if (prtcl.outputChar.equals("ctrly")) {
								doType(VK_CONTROL, VK_Y);
							} else if (prtcl.outputChar.equals("ctrlc")) {
								doType(VK_CONTROL, VK_C);
							} else if (prtcl.outputChar.equals("ctrlv")) {
								doType(VK_CONTROL, VK_V);
							} else if (prtcl.outputChar.equals("ctrla")) {
								doType(VK_CONTROL, VK_A);
							} else if (prtcl.outputChar.equals("ctrlx")) {
								doType(VK_CONTROL, VK_X);
							} else if (prtcl.outputChar.equals("ctrlp")) {
								doType(VK_CONTROL, VK_P);
							} else if (prtcl.outputChar.equals("Caps Lock")) {
								doType(VK_CAPS_LOCK);
							} else if (prtcl.outputChar.equals("Num Lock")) {
								doType(VK_NUM_LOCK);
							} else if (prtcl.outputChar.equals("Win")) {
								doType(VK_WINDOWS);
							} else if (prtcl.outputChar.equals("Del")) {
								doType(VK_DELETE);
							} else if (prtcl.outputChar.equals("Insert")) {
								doType(VK_INSERT);
							} else if (prtcl.outputChar.equals("Home")) {
								doType(VK_HOME);
							} else if (prtcl.outputChar.equals("End")) {
								doType(VK_END);
							} else if (prtcl.outputChar.equals("Page Up")) {
								doType(VK_PAGE_UP);
							} else if (prtcl.outputChar.equals("Page Down")) {
								doType(VK_PAGE_DOWN);
							} else if (prtcl.outputChar.equals("Ctrl+S")) {
								doType(VK_CONTROL, VK_S);
							} else if (prtcl.outputChar.equals("Ctrl+Alt+Del")) {
								doType(VK_CONTROL, VK_SHIFT, VK_ESCAPE);
							} else if (prtcl.outputChar.equals("Alt+Enter")) {
								doType(VK_ALT, VK_ENTER);
							} else if (prtcl.outputChar.equals("Alt+Tab")) {
								//OMG Thats HACK!
								Runtime.getRuntime().exec("cmd /c" + "%windir%\\explorer.exe shell:::{3080F90E-D7AD-11D9-BD98-0000947B0257}");
							} else if (prtcl.outputChar.equals("ctrl_shift_z")) {
								doType(VK_CONTROL, VK_SHIFT, VK_Z);
							} else if (prtcl.outputChar.equals("f1")) {
								doType(VK_F1);
							} else if (prtcl.outputChar.equals("f2")) {
								doType(VK_F2);
							} else if (prtcl.outputChar.equals("f3")) {
								doType(VK_F3);
							} else if (prtcl.outputChar.equals("f4")) {
								doType(VK_F5);
							} else if (prtcl.outputChar.equals("f5")) {
								doType(VK_F5);
							} else if (prtcl.outputChar.equals("f6")) {
								doType(VK_F6);
							} else if (prtcl.outputChar.equals("f7")) {
								doType(VK_F7);
							} else if (prtcl.outputChar.equals("f8")) {
								doType(VK_F8);
							} else if (prtcl.outputChar.equals("f9")) {
								doType(VK_F9);
							} else if (prtcl.outputChar.equals("f10")) {
								doType(VK_F10);
							} else if (prtcl.outputChar.equals("f11")) {
								doType(VK_F11);
							} else if (prtcl.outputChar.equals("f12")) {
								doType(VK_F12);
							} else if (prtcl.outputChar.equals("down")) {
								doType(VK_DOWN);
							} else if (prtcl.outputChar.equals("left")) {
								doType(VK_LEFT);
							} else if (prtcl.outputChar.equals("right")) {
								doType(VK_RIGHT);
							} else if (prtcl.outputChar.equals("esc")) {
								doType(VK_ESCAPE);
							} else if (prtcl.outputChar.equals("enter")) {
								doType(VK_ENTER);
							} else if (prtcl.outputChar.equals("contextMenu")) {
								doType(VK_CONTEXT_MENU);
							} else if (isChrEnter) {
								doType(VK_ENTER);
							} else if (isChrSpace) {
								doType(VK_SPACE);
							} else {
								stringSelection = new StringSelection(prtcl.outputChar);
								clpbrd.setContents(stringSelection, null);

								doType(VK_CONTROL, VK_V);
							}


							//robot.keyPress(Character.toUpperCase(prtcl.outputChar.charAt(0)));
						} catch (AWTException e) {

							e.printStackTrace();
						}
						replyServerNameAndProcess(out);
						break;

					case Protocol.commandLine:

						if (OSValidator.isWindows()) {
							if (prtcl.outputCommandLine.startsWith("ahk ")) {
								String home = System.getProperty("user.home");
								PrintWriter outahk = new PrintWriter(home + "/ahk.ahk");
								String[] lines = prtcl.outputCommandLine.replace("ahk ", "").split("\n");
								for (String line1 : lines) {
									outahk.println(line1);
								}
								outahk.close();
								Runtime.getRuntime().exec("cmd /c" + "%USERPROFILE%\\AutoHotkey.exe %USERPROFILE%\\ahk.ahk");

							} else if (prtcl.outputCommandLine.startsWith("powershell ")) {

								String home = System.getProperty("user.home");
								PrintWriter outPowershell = new PrintWriter(home + "/powershell.ps1");
								String[] lines = prtcl.outputCommandLine.replace("powershell ", "").split("\n");
								for (String line1 : lines) {
									outPowershell.println(line1);
								}

								outPowershell.close();
								Runtime.getRuntime().exec("cmd /c" + "powershell -executionpolicy remotesigned -file %USERPROFILE%\\powershell.ps1");

							} else {
								Runtime.getRuntime().exec("cmd /c" + prtcl.outputCommandLine.replace("nircmdc", "%USERPROFILE%\\nircmdc"));
							}

						} else if (OSValidator.isUnix()) {
							Runtime.getRuntime().exec(prtcl.outputCommandLine);
						}
						replyServerNameAndProcess(out);

						break;

					case Protocol.forward:
						forwardSocket(prtcl.outputCommandLine, prtcl.outputForwardPort);
						replyServerNameAndProcess(out);
						break;

					case Protocol.overlay:
						if (prtcl.overlayStrings.length == 9) {
							drawOverlay(prtcl.overlayStrings);
						}
						moveFocus(prtcl.outputOverlayNumber);
						break;

					case Protocol.shortcut:
						String[] buttons = prtcl.outputShortcut.split(" \\+ ");
						int[] keyCodes = new int[buttons.length];
						for (int i = 0; i < buttons.length; i++) {
							keyCodes[i] = buttonStringToInt(buttons[i]);
						}
						doType(keyCodes);
						replyServerNameAndProcess(out);
						break;

					case Protocol.register:

						String ip = socket.getInetAddress().getHostAddress();
						if (!clientIps.contains(ip + ":" + prtcl.outputPort)) {
							System.out.println("rtying to register:" + ip + ":" + prtcl.outputPort);

							clientIps.add(ip + ":" + prtcl.outputPort);

							out.writeUTF("registred");
							out.flush();

							System.out.println("registred:" + ip + ":" + prtcl.outputPort);
						} else {
							clientIps.remove(ip + ":" + prtcl.outputPort);
							out.writeUTF("unregistred");
							out.flush();

							System.out.println("unregistred:" + ip + ":" + prtcl.outputPort);
						}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

	}

	private void drawOverlay(String[] actions) {
		//Рисуем в свинге
		contextBtnOverlay = new JDialog();


		contextBtnOverlay.setAlwaysOnTop(true);
		PointerInfo poInfo = MouseInfo.getPointerInfo();
		Point poLoc = poInfo.getLocation();
		contextBtnOverlay.setLocation(poLoc);
		contextBtnOverlay.setUndecorated(true);
		contextBtnOverlay.setOpacity(0.8f);
		for (int i = 0; i < actions.length; i++) {
			actions[i] = "<td>" + actions[i] + "</td>";
		}

		this.actions = actions;
		labelSqare = new JLabel("<html>" +
				"<head>" +
				"<style>" +
				"td {text-align: center;    border: 1px solid black;    padding: 5px;}" +
				"table {    border-spacing: 15px;}" +
				"</style>" +
				"</head>" +
				"<body>" +
				"<table style=\"width:100%\">" +
				"<tr>" +
				actions[0] +
				actions[1] +
				actions[2] +
				"</tr>" +

				"<tr>" +
				actions[3] +
				actions[4] +
				actions[5] +
				"</tr>" +

				"<tr>" +
				actions[6] +
				actions[7] +
				actions[8] +
				"</tr>" +
				"</table>" +
				"</body>" +


				"</html>", SwingConstants.CENTER);

		labelSqare.setHorizontalTextPosition(JLabel.CENTER);


		contextBtnOverlay.add(labelSqare);
		contextBtnOverlay.pack();
		contextBtnOverlay.getContentPane().setBackground(Color.WHITE);

		contextBtnOverlay.setVisible(true);

	}

	private void closeOverlay() {
		if (contextBtnOverlay != null)
			contextBtnOverlay.dispatchEvent(new WindowEvent(contextBtnOverlay, WindowEvent.WINDOW_CLOSING));
	}

	private void moveFocus(int i) {
		if (i > 0) {
			i--;
			String oldAction = actions[i];
			actions[i] = actions[i].replace("<td>", "<td bgcolor=\"#0080FF\"><FONT COLOR=WHITE>").replace("</td>", "</FONT></td>");

			Font font = labelSqare.getFont();
// same font but bold
			Font boldFont = new Font(font.getFontName(), Font.PLAIN, font.getSize());
			labelSqare.setFont(boldFont);

			labelSqare.setText("<html>" +
					"<head>" +
					"<style>" +
					"td { text-align: center;   border: 1px solid black;    padding: 5px;}" +
					//"th { font-style:normal; color:white; text-align: center;   border: 1px solid black;    padding: 5px;}"+
					"table {    border-spacing: 15px;}" +
					"</style>" +
					"</head>" +
					"<body>" +

					"<table style=\"width:100%\">" +
					"<tr>" +
					actions[0] +
					actions[1] +
					actions[2] +
					"</tr>" +

					"<tr>" +
					actions[3] +
					actions[4] +
					actions[5] +
					"</tr>" +

					"<tr>" +
					actions[6] +
					actions[7] +
					actions[8] +
					"</tr>" +
					"</table>" +
					"</body>" +
					"</html>");
			actions[i] = oldAction;
			labelSqare.setHorizontalTextPosition(JLabel.CENTER);
			labelSqare.setVerticalTextPosition(JLabel.BOTTOM);
			labelSqare.revalidate();
			labelSqare.repaint();
			contextBtnOverlay.revalidate();
			contextBtnOverlay.repaint();
		} else {
			closeOverlay();
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

						out.writeUTF("ok" + localMachine.getHostName() + "<process>" + getActiveWindowProccessName());
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}).start();
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}
	}

	public void say(String say) {
		for (String clientIpPort : clientIps) {
			InputStream sin;
			String ip = null;
			int port = -1;
			int delimetr;
			try {


				delimetr = clientIpPort.indexOf(":");
				ip = clientIpPort.substring(0, delimetr);
				port = Integer.parseInt(clientIpPort.substring(delimetr + 1));


				System.out.println("trying say:" + ip + ":" + port);

				InetAddress ipAddress = InetAddress.getByName(ip);
				Socket clientSocket = new Socket(ipAddress, port);
				sin = clientSocket.getInputStream();

				OutputStream sout = clientSocket.getOutputStream();


				DataInputStream in = new DataInputStream(sin);
				DataOutputStream out = new DataOutputStream(sout);

				say = r.get();
				out.writeUTF("results:" + say);
				out.flush();
				clientSocket.close();
			} catch (IOException e) {
				clientIps.remove(ip + ":" + port);

				System.out.println("Ooops, that was exeption, i'l better remove it:" + ip + ":" + port);
				e.printStackTrace();
			}
		}
	}

	public void forwardSocket(String say, int port) {

		InputStream sin;
		String ip;
		InetAddress ipAddress;
		try {

			ipAddress = SocketKeeper.getFirstNonLoopbackAddress(true, false);
			ip = ipAddress.toString();

			System.out.println("trying forward:" + ip + ":" + port + " | " + say);

			Socket clientSocket = new Socket(ipAddress, port);
			sin = clientSocket.getInputStream();

			OutputStream sout = clientSocket.getOutputStream();

			DataInputStream in = new DataInputStream(sin);
			DataOutputStream out = new DataOutputStream(sout);
			out.writeUTF(say);
			out.flush();
			clientSocket.close();
		} catch (IOException e) {

			System.out.println("Can't forward:" + port);
			e.printStackTrace();
		}

	}

	//Util
	private void doType(int... keyCodes) {
		//Alt + arrows java bug work-around (for chrome)
		if (keyCodes.length == 2 && Arrays.asList(keyCodes).contains(VK_ALT) && (Arrays.asList(keyCodes).contains(VK_LEFT) || Arrays.asList(keyCodes).contains(VK_RIGHT) || Arrays.asList(keyCodes).contains(VK_UP) || Arrays.asList(keyCodes).contains(VK_DOWN))) {
			doType(VK_ALT);
		}

		try {
			doType(keyCodes, 0, keyCodes.length);
		} catch (IllegalArgumentException e) {
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

	private int buttonStringToInt(String s) {
		int inttoRetturn = 1;
		if (s.length() == 1) {
			return KeyEvent.getExtendedKeyCodeForChar(s.charAt(0));
		} else {
			if (s.equals("Ctrl")) {
				inttoRetturn = VK_CONTROL;

			} else if (s.equals("Alt")) {
				inttoRetturn = VK_ALT;

			} else if (s.equals("Shift")) {
				inttoRetturn = VK_SHIFT;

			} else if (s.equals("Tab")) {
				inttoRetturn = VK_TAB;

			} else if (s.equals("Win")) {
				if (OSValidator.isWindows()) {
					inttoRetturn = VK_WINDOWS;
				} else if (OSValidator.isUnix()) {
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

			} else if (s.equals("Plus")) {
				inttoRetturn = VK_ADD;

			} else if (s.equals("Minus")) {
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

	private String getActiveWindowProccessName() {
		if (OSValidator.isWindows()) {
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
			String nameString = Native.toString(name);
			System.out.println(nameString);

			return nameString;
			//<----------

		} else if (OSValidator.isUnix()) {
//            try {
//                Process p = Runtime.getRuntime().exec("xdotool getwindowfocus getwindowname");
//
//                InputStream stderr = p.getErrorStream();
//                InputStreamReader isr2 = new InputStreamReader(stderr);
//                BufferedReader br2 = new BufferedReader(isr2);
//                String line2 = null;
//                System.out.println("<ERROR>");
//                while ( (line2= br2.readLine()) != null)
//                    System.out.println(line2);
//                System.out.println("</ERROR>");
//
//
//
//                String toRet ="";
//                InputStream stdin = p.getInputStream();
//                InputStreamReader isr = new InputStreamReader(stdin);
//                BufferedReader br = new BufferedReader(isr);
//                String line = null;
//                System.out.println("<OUTPUT>");
//                while ( (line = br.readLine()) != null){
//                    System.out.println(line);
//                    toRet+=line;
//                }
//
//                System.out.println("</OUTPUT>");
//                return (toRet.lastIndexOf(" - ")!=-1)?toRet.substring(toRet.lastIndexOf(" - ")+3):"nautilus";
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
			return "";
		} else {
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

	public List<WindowInfo> getWindowsProcessesTitles() {
		final List<WindowInfo> inflList = new ArrayList<WindowInfo>();
		final List<Integer> order = new ArrayList<Integer>();
		int top = User32.instance.GetTopWindow(0);
		while (top != 0) {
			order.add(top);
			top = User32.instance.GetWindow(top, User32.GW_HWNDNEXT);
		}
		User32.instance.EnumWindows(new WndEnumProc() {
			public boolean callback(int hWnd, int lParam) {
				if (User32.instance.IsWindowVisible(hWnd)) {


					byte[] buffer = new byte[1024];
					byte[] name = new byte[2048];

					IntByReference pid = new IntByReference();
					PsApi psapi = (PsApi) Native.loadLibrary("psapi", PsApi.class);
					User32.instance.GetWindowThreadProcessId(hWnd, pid);
					WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, pid.getValue());
					psapi.GetProcessImageFileNameA(process, name, 2048);
					String nameString = Native.toString(name);

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

		int GetModuleFileNameExA(WinNT.HANDLE process, WinNT.HANDLE module,
								 byte[] name, int i);

		int GetModuleFileNameExW(WinNT.HANDLE process, WinNT.HANDLE module,
								 byte[] name, int i);

		int GetProcessImageFileNameW(WinNT.HANDLE hProcess, byte[] lpImageFileName,
									 int nSize);

		int GetProcessImageFileNameA(WinNT.HANDLE hProcess, byte[] lpImageFileName,
									 int nSize);

	}

	public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
		boolean callback(int hWnd, int lParam);
	}

	public static interface User32 extends StdCallLibrary {
		final User32 instance = (User32) Native.loadLibrary("user32", User32.class);

		boolean EnumWindows(WndEnumProc wndenumproc, int lParam);

		boolean ShowWindow(int hwnd, int i);

		boolean SetForegroundWindow(int hwnd);

		void GetWindowThreadProcessId(int hWnd, IntByReference pid);

		boolean IsWindowVisible(int hWnd);

		boolean IsIconic(int hWnd);

		boolean LockSetForegroundWindow(int i);

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

		public WindowInfo(int hwnd, String title, String process) {
			this.hwnd = hwnd;
			this.title = title;
			this.process = process;
		}

		public String toString() {
			return title + "|" + process;
		}
	}


}
