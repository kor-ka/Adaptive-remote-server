import java.net.*;
import java.util.Queue;
import java.io.*;
public class SocketKeeper {
	  static int port;
   public static void main(String[] ar)    {
	  
       try {
    	   System.out.println("Host IP: "+InetAddress.getLocalHost().getHostAddress());
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
}
