import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
public class SocketKeeper {
	  static int port;
   public static void main(String[] ar)    {
	  
       try {
    	  
    	     System.out.println("Gimme a POOORT!");
    	     BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    	     String portLine = null;
    	   
    	 portLine = keyboard.readLine();
    	 try {
				port = Integer.parseInt(portLine);
				
			} catch (Exception e) {
				
			}
    	 while(port <= 1024){
    		 System.out.println("R u kidding me? ");
    		 System.out.println("Try again (by the way, the port number must be greater than 1024)");	
    		 portLine = keyboard.readLine();    
    		 try {
				port = Integer.parseInt(portLine);
				
			} catch (Exception e) {
				
			}
    	 }
    	 writeAdress(port);
    	 ServerSocket ss = new ServerSocket(port); 
    	
    	 Qqueue q = new Qqueue();
    	 Results r = new Results();
    	 
    	 final Counter cntr = new Counter(q,r);
    	 new Thread(cntr).start();
    	
    	 final ListenServer ls = new ListenServer(ss, q, r);
    	 new Thread(ls).start();
    	 while(true){
    		 final String say = keyboard.readLine();
			 if(say !=null){
				 System.out.println("You going 2 say: " + say);

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
   public static void writeAdress(int port){
	   
	   try {
		   
		   
		   
		System.out.println("Host IP+Port: "+InetAddress.getLocalHost().getHostAddress()+":"+port);
	
		
		BitMatrix matrix = new MultiFormatWriter().encode(InetAddress.getLocalHost().getHostAddress()+":"+port, BarcodeFormat.QR_CODE, 20, 20);
		
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100,150);
        
        //create a label for a heart shape
        JLabel labelSqare = new JLabel("\u25A0"+"\u0020"+"\u25A0"+"\u2386"+"\u25A0");  
        
        frame.add(labelSqare);
        frame.setVisible(true);
		
		for (int h = 0; h<matrix.getHeight(); h++){
			for(int w = 0; w<matrix.getWidth(); w++){
				if(matrix.get(h, w)){
					System.out.print("||");
					
				}else{
					
					
					System.out.print("  ");
				}
				if(w==matrix.getWidth()-1){
					System.out.println("");
				}
			}
			
		}
	   } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
	   
}
