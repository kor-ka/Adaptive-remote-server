import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

        import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jssc.SerialPortList;

        import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.jna.platform.FileUtils;

public class SocketKeeper {
    static int port;
    public static void main(String[] ar) throws IOException {
        ServerSocket ss;
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
       
      
        
        if (ar[0]==null) {
			if (OSValidator.isWindows()) {
				String sysdir = System.getProperty("user.home");
				String nircmdcPath = sysdir + "/nircmdc.exe";
				File nircmdc = new File(nircmdcPath);
				if (!nircmdc.exists()) {
					nircmdc.createNewFile();
					InputStream in = SocketKeeper.class
							.getResourceAsStream("res/nircmdc.exe");

					OutputStream outStream = new FileOutputStream(nircmdc);

					byte[] buffer = new byte[8 * 1024];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
					}
					in.close();
					outStream.close();
				}
			}
			try {

				port = 1025;
				while (true) {

					try {
						port += 1;//(int)(Math.random() * ((65535 - 1025) + 1));
						if (port > 1031)
							port = 1025;
						ss = new ServerSocket(port);
						ss.setReuseAddress(true);
						new Thread(new Runnable() {
							public void run() {
								writeAdress(port);
								return;
							}
						}).start();

						break;
					} catch (Exception e) {

					}
				}

				Qqueue q = new Qqueue();
				Results r = new Results();

				final Counter cntr = new Counter(q, r);
				new Thread(cntr).start();

				final ListenServer ls = new ListenServer(ss, q, r);
				new Thread(ls).start();
				while (true) {
					final String say = keyboard.readLine();
					if (say != null) {

						writeAdress(port);

						new Thread(new Runnable() {
							public void run() {
								ls.say(say);
								return;
							}
						}).start();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			
			switch (stringToCommand(ar[0])) {
			case PORT_NAMES:
				 System.out.println("Prt nms");
				  String[] portNames = SerialPortList.getPortNames();
			        for(int i = 0; i < portNames.length; i++){
			            System.out.println(portNames[i]);
			        }
				break;

			case WAT:
				System.out.println("Hm.");
				break;
			}
			
			
		}
    }
    
   
    public static final int PORT_NAMES = 1;
    public static final int WAT = 0;
    
    static int stringToCommand(String s){
		if(s.equals("pn")) return PORT_NAMES;
		return WAT;
    	
    }

    //UTIL
    /**
     * @param port
     */
    public static void writeAdress(int port){

        try {

            System.out.println("Host IP+Port: "+getFirstNonLoopbackAddress(true,false).getHostAddress()+":"+port);


            //Ð ÐžÑÑÐµÐŒ Ð² ÑÐ²ÐžÐœÐ³Ðµ
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(170,320);

            BufferedImage image = getQrImage(port);

            JLabel labelSqare = getjLabel(port, image, null);
            frame.add(labelSqare);
            frame.getContentPane().setBackground(Color.WHITE);
            frame.setIconImage(image);
            frame.setVisible(true);

            String oldAdress =  getFirstNonLoopbackAddress(true,false).getHostAddress()+":"+port;
            String newAdress;

            do{
                newAdress = getFirstNonLoopbackAddress(true,false).getHostAddress()+":"+port;
                if(!newAdress.equals(oldAdress)){
                    image = getQrImage(port);
                    labelSqare = getjLabel(port, image, labelSqare);
                    labelSqare.revalidate();
                    labelSqare.repaint();
                    frame.revalidate();
                    frame.repaint();

                }
                oldAdress =  newAdress;
            }while(true);



        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static JLabel getjLabel(int port, BufferedImage image, JLabel labelSqare) throws SocketException {

        if(labelSqare == null)labelSqare = new JLabel(new ImageIcon(image));

        labelSqare.setIcon(new ImageIcon(image));
        labelSqare.setText("<html><center>QR для настройки<br/>" +
                "Adaptive remote.<br/>" +
                "Для настройки вручную:"+"<br/>"+
                "<br/>"+
                "IP:   "+getFirstNonLoopbackAddress(true,false).getHostAddress()+"<br/>"+
                "Port: "+port+"<br/>"+"</center></html>");
        labelSqare.setHorizontalTextPosition(JLabel.CENTER);
        labelSqare.setVerticalTextPosition(JLabel.BOTTOM);
        return labelSqare;
    }

    private static BufferedImage getQrImage(int port) throws WriterException, SocketException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(getFirstNonLoopbackAddress(true,false).getHostAddress()+":"+port, BarcodeFormat.QR_CODE, 170, 170, hintMap);
        //Ð¡ÐŸÐ±ÐžÑÐ°ÐµÐŒ ÐºÐ°ÑÑÐžÐœÐºÑ

        int matrixWidth = bitMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);

        Color mainColor = new Color(1, 1, 1);
        graphics.setColor(mainColor);

        //Write Bit Matrix as image
        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
    }

    //Utils
    public static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }

}