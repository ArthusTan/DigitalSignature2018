package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Algorithm.Algor;

public class Client{
	private static String KIP = "127.0.0.1";
	private static String SIP = "127.0.0.1";
	private static int KPORT = 8888;
	private static int SPORT = 7777;
	private static String TGT = ",,";
	private static String userName = "";
	private static String password = "";
	private static String sessionKey = "";
	private static String ticket = "";
	
	private static String sendMsg (Socket socket,
								   PrintWriter pWriter,
								   BufferedReader bReader,
								   String message){//Send message to Server
		pWriter.println(message);
		pWriter.flush();
		System.out.println("\tClient            : " + message);
		
		String response = "";
        try {
			response = bReader.readLine();
		} catch (Exception e) { System.out.println("cannot listen to the server."); }
        
		return new String(Algor.decipher(response, password));
	}
	
	public static void main(String[] args){
        try {
        	//建立连接
            Socket socket = new Socket(KIP,KPORT);
            System.out.println("--- CLIENT ---");
            
            BufferedReader inputer = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter    pWriter = new PrintWriter(socket.getOutputStream());
	        BufferedReader bReader = new BufferedReader(
	        		new InputStreamReader(socket.getInputStream()));
	        
            //---login---
	        System.out.print("USERNAME : ");
            userName = inputer.readLine();
            System.out.print("PASSWORD : ");
            password = inputer.readLine();
            
            String message = "login," + userName + "," + password;
            message = sendMsg(socket,
            				  pWriter,
					  		  bReader,
					  		  message);//response
            System.out.println("\tAServer[DECIPHER] : " + message);
            
            //---login successful---
            if(!message.equals("[QUIT] Warn username or password.")){
            	//---visit---
	            System.out.print("VISIT    : ");
            	message = inputer.readLine();//target
            	message = "visit," + userName + "," + message;
            	System.out.println("\tClient[DECIPHER]  : " + message);
            	message = sendMsg(socket,
      				  			  pWriter,
      				  			  bReader,
      				  			  new String(Algor.encrypt(message,password)));//response
            	TGT = message;
            	System.out.println("\tAServer[DECIPHER] : (TGT)    : " + message);
            	System.out.println();
            	
            	//---allow to visit---
            	if(!message.equals("[QUIT] Donot have enough power to visit it.")){
            		
            		//---ask for ticket---
            		message = "askTicket,"+TGT;
            		System.out.println("\tClient[DECIPHER]  : " + message);
                	message = sendMsg(socket,
            						  pWriter,
            						  bReader,
            						  new String(Algor.encrypt(message, password)));//response
            		System.out.println("\tTGServer[DECIPHER]: (Ticket) : " + message);
            		sessionKey = message.split(",and,")[1];
            		System.out.println("[SESSION KEY] = " + sessionKey);
            		ticket = message.split(",")[0];
            		System.out.println("[TICKET]      = " + ticket);
            		
            		//---ticket isn't illegal---
            		if(!ticket.equals("[QUIT] TGT is illegal.")){
            			
            			System.out.println();
            			System.out.println("--- BEGIN TO VISIT WEB SERVER---");
            			
            			//---visit Web Server---
            			Socket webSocket = new Socket(SIP, SPORT);
            			
            			System.out.println("--- TRYING TO CONNECT WEB SERVER ---");
                        
                        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                        
                        PrintWriter    pw = new PrintWriter(webSocket.getOutputStream());
            	        BufferedReader br = new BufferedReader(
            	        		new InputStreamReader(webSocket.getInputStream()));
            	        
            	        String response = null;
            	        response = new String(Algor.encrypt(sendMsg(webSocket,
            	        										  pw,
            	        										  br,
            	        										  ticket),password));
            	        if(!response.equals("[QUIT] WRONG TICKET")){
	            	        response = new String(Algor.decipher(response, sessionKey));
	            	        
	            	        System.out.println("FROM WEB SERVER           : " + response);
	            	        
	            	        System.out.println("--- BEGIN TO VISIT ---");
	            			while(true){
	            				;
	            				/*
	    		            	 * BEGGIN TO VISIT THE RESOURCE LEGALLY
	    		            	 */
	            			}
            			}
	            	}
            	}
            }
            
            pWriter.close();
            bReader.close();
            socket.close();
        } catch (Exception e) {System.out.println("cannot listen to the server.");
        }finally{
        	System.out.println("--- END  ---");
        }
    }

}