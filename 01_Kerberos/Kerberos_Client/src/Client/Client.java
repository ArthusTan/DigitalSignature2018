package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Algorithm.Algor;

public class Client{
	private static int    KPORT = 8888;
	private static int    SPORT = 7777;
	private static String KIP = "127.0.0.1";
	private static String SIP = "127.0.0.1";
	private static String sessionKey = "";
	private static String TGT = "";
	private static String ticket = "";
	private static String webName = "";
	private static String userName = "";
	private static String password = ""; // Plan to use MD5 in the future version.
										 // Input a real-password and get its Hash-value(MD5).
										 // Compare with the Hash-value in database of K_Server. 

	private static String sendMsg (Socket socket,
								   PrintWriter pWriter,
								   BufferedReader bReader,
								   String message){// Send [message] to Server
		pWriter.println(message);// Send [message]
		pWriter.flush();
		System.out.println("\tClient            : " + message);
		String response = "";
        try {
			response = bReader.readLine();// Get Response[Encrypted]
		} catch (Exception e) {
			System.out.println("cannot listen to the server.");
		}
		return new String(Algor.decipher(response, password));// Return Response[Deciphered]
	}
	public static void main(String[] args){
        try {
            Socket socket = new Socket(KIP,KPORT);//Listen to K_Server
            System.out.println("--- CLIENT ---");
            BufferedReader inputer = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter    pWriter = new PrintWriter(socket.getOutputStream());
	        BufferedReader bReader = new BufferedReader(
	        		new InputStreamReader(socket.getInputStream()));
	        
	        //---login---
	        System.out.print("USERNAME : ");
	        userName = inputer.readLine(); // Get UserName
            System.out.print("PASSWORD : ");
            password = inputer.readLine(); // Get Password (Plan to use MD5 in the future version)
            String message = "login," + userName + "," + password; // Send Login Order(Ask TGT)
            message = sendMsg(socket,
            				  pWriter,
					  		  bReader,
					  		  message);// Get Login Response (TGT)
            TGT = message; // Get Ticket-Granting Ticket
            System.out.println("\tAServer[DECIPHER] : (TGT) : " + message);
            
            //---login successful, ask ticket---
            if(!message.equals("[QUIT] Warn username or password.")){
            	
            	//---visit---
	            System.out.print("VISIT    : ");
            	webName = inputer.readLine(); // Get Name[Web Server]
            								  // Here we use user_00 for default web server.
            								  // If not, please change the value in web server(WebServer.java).
            	message = "visit,and," + webName + ",and," + TGT; 
            	System.out.println("\tClient[DECIPHER]  : " + message);
            	message = sendMsg(socket,
      				  			  pWriter,
      				  			  bReader,
      				  			  new String(Algor.encrypt(message,password))); // Ask Ticket (with TGT)
            	System.out.println("\tTGServer[DECIPHER]: (Ticket, SK) : " + message);
            	sessionKey = message.split(",and,")[1]; // Get Session Key
            	ticket = message.split(",and,")[0];		// Get Ticket
            	System.out.println("[SESSION KEY] = " + sessionKey); // Show Session Key
            	System.out.println("[TICKET]      = " + ticket); 	 // Show Ticket
            	
            	//---ticket is legal---
            	if(!ticket.equals("[QUIT] TGT is illegal.")){
            		System.out.println("\n--- BEGIN TO VISIT WEB SERVER---");
            		
            		//---visit Web Server---
            		Socket webSocket = new Socket(SIP, SPORT); // Listen to Web Server
            		System.out.println("--- TRYING TO CONNECT WEB SERVER ---");
                    PrintWriter    pw = new PrintWriter(webSocket.getOutputStream());
            	    BufferedReader br = new BufferedReader(
            	    		new InputStreamReader(webSocket.getInputStream()));
            	    String response = new String(Algor.encrypt(sendMsg(webSocket,
            	    										  		   pw,
            	       										  		   br,
            	       										  		   ticket), // Send Ticket
            	       							 password)); // Ask Allowance[Encrypted by Session Key]
            	    if(!response.equals("[QUIT] WRONG TICKET")){
	                    response = new String(Algor.decipher(response, sessionKey));   // Get Allowance
	           	        System.out.println("FROM WEB SERVER           : " + response);
	                    System.out.println("--- BEGIN TO VISIT ---");
	            		while(true){;
	           				/**
	    	            	 * BEGGIN TO VISIT WEB SERVER
	    	            	 **/
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