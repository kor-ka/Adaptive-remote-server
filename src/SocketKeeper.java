import java.net.*;
import java.io.*;
public class SocketKeeper {
	  static int port;
   public static void main(String[] ar)    {
     
     
       try {
    	   
    	  
    	     System.out.println("Gimme a POOORT!");
    	     BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    	     String portLine = null;
    	   
    	 portLine = keyboard.readLine();
    	 port = Integer.parseInt(portLine);
    	 while(port==-1){
    		 System.out.println("Are f**king kidding me? ");
    		 System.out.println("Try again");	
    		 portLine = keyboard.readLine();    	 
    	 }
    	 ServerSocket ss = new ServerSocket(port); 
    	
    	 
    	
    	 ListenServer ls = new ListenServer(ss);
    	 new Thread(ls).start();
    	 while(true){
    		 String say = keyboard.readLine();
        	ls.say(say);
    	 }
    	
      } catch(Exception x) { x.printStackTrace(); }
   }
}
