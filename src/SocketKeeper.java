import java.net.*;
import java.io.*;
public class SocketKeeper {
   public static void main(String[] ar)    {
     int port=-1;
     
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
    	 
    	 while(true) {
         ServerSocket ss = new ServerSocket(port); 
         System.out.println("Waiting for a client...");

         Socket socket = ss.accept(); 
         System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
         System.out.println();

 
         InputStream sin = socket.getInputStream();
         OutputStream sout = socket.getOutputStream();


         DataInputStream in = new DataInputStream(sin);
         DataOutputStream out = new DataOutputStream(sout);

         String line = null;
         
           line = in.readUTF(); 
           System.out.println("Client say: " + line);
           protocol prtcl =  new protocol(); 
           switch(prtcl.processInput(line)){
           case protocol.ab:
        	   System.out.println("a:"+ prtcl.outputA);
        	   System.out.println("b:"+ prtcl.outputB);
               out.writeUTF("lol, that was ab!"); 
               out.flush();
               
        	   break;
        	   
           case protocol.register:
        	  
               out.writeUTF("register? nope."); 
               out.flush(); 
               
        	   System.out.println("register? nope.");
           }
          
          
           		
        	   socket.close();
        	   ss.close();
        	   
           
         }
      } catch(Exception x) { x.printStackTrace(); }
   }
}
