import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
public class SocketKeeper {
	  static int port;
   public static void main(String[] ar)    {
	   ServerSocket ss;
	   BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
       try {
    	  
    	    
    	 while(true){
    		 
    		 try {
				port = 1025 + (int)(Math.random() * ((65535 - 1025) + 1));
				ss = new ServerSocket(port); 
				writeAdress(port);
				break;
			} catch (Exception e) {
				
			}
    	 }
    	
    	 
    	
    	 Qqueue q = new Qqueue();
    	 Results r = new Results();
    	 
    	 final Counter cntr = new Counter(q,r);
    	 new Thread(cntr).start();
    	
    	 final ListenServer ls = new ListenServer(ss, q, r);
    	 new Thread(ls).start();
    	 while(true){
    		 final String say = keyboard.readLine();
			 if(say !=null){
				 writeAdress(port);
				 new Thread(new Runnable(){
						 public void run (){
							 ls.say(say);
							 return;
						 }
					 }).start();
			 }
    		 
    	 }
    	
      } catch(Exception e) { e.printStackTrace(); }
   }
   
   //UTIL
   /**
 * @param port
 */
public static void writeAdress(int port){
	   
	   try {
		   
		   
		   
		System.out.println("Host IP+Port: "+InetAddress.getLocalHost().getHostAddress()+":"+port);
	
		//Собираем QR
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(InetAddress.getLocalHost().getHostAddress()+":"+port, BarcodeFormat.QR_CODE, 100, 100, hintMap);
		//Собираем картинку
		
		int matrixWidth = bitMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();

		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);

		Color mainColor = new Color(51, 102, 153);
		graphics.setColor(mainColor);
		 
		//Write Bit Matrix as image
		for (int i = 0; i < matrixWidth; i++) {
		    for (int j = 0; j < matrixWidth; j++) {
		        if (bitMatrix.get(i, j)) {
		            graphics.fillRect(i, j, 1, 1);
		        }
		    }
		}
		
		//Рисуем в свинге
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(150,150);
        
       
        ImageIcon icon = new ImageIcon(image);
        JLabel labelSqare = new JLabel(icon);  
        
        frame.add(labelSqare);
        frame.setVisible(true);
		
		
	   } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
	   
}
