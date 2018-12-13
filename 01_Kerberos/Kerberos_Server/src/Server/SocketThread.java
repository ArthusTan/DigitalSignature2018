package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Algorithm.Algor;
import Function.AServer;
import Function.TGServer;

public class SocketThread extends Thread{
	private Socket socket = null;
	private String target = "";
	private static String webKey = "";
	private static String ServerKey = "kerberos";// AS_Key & TGS_Key
	private static String password = "";
	private static String userName = "";
	
	public SocketThread(Socket socket){
		this.socket = socket;
		this.target = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}
	
	private static boolean reciMsg (Socket socket,
									PrintWriter    pWriter,
									BufferedReader bReader,
									String target){//Receive message, then analysis it.
		String message = "";
		boolean flag = true;
		try {
			message = bReader.readLine();
			System.out.println("Client[RECI]           : " + message);
		} catch (Exception e) { return false; }
		String response = "get message from " + target; // Default Response (Never gonna happen)
		
	////---begin to analysis the message---
		
		//---login---
		if(message.startsWith("login")){
			System.out.println("\t>>> LOGIN   <<<");
			String[] array = message.split(",");
			userName = array[1];
			password = array[2];
			if(AServer.confirm(message)){
				System.out.println("\t>>> SUCCESS <<<");
				response = "[AUTH]," + userName;	// Ticket-Granting Ticket
				System.out.println("(TGT) : " + response);
				response = new String(Algor.encrypt(response, ServerKey));
				System.out.println("(TGT) : " + response);
			}
			else{
				System.out.println("\t>>> FAILED  <<<");
				response = "[QUIT] Warn username or password.";
				flag = false;
			}
		}
		else{
			
			//---get real message---
			message = new String(Algor.decipher(message, password));
			System.out.println("Client[RECI][DECIPHER] : " + message);
			
			//---visit---
			if(message.startsWith("visit")){
				System.out.println("\t>>> VISIT   <<<");
				String[] array = message.split(",and,");
				String webName = array[1];
				String TGT = new String(Algor.decipher(array[2], ServerKey));
				System.out.println("\t>>> GET Ticket-Granting Ticket : " + TGT);
				
				String ticket = TGServer.confirm(webName,TGT);
				if(!ticket.equals("[NOTALLOW]")){
					//---get webKey---
					webKey = AServer.visitKey(webName);
					System.out.println("\t>>> GET WEBKEY : " + webKey);
					System.out.println("\t>>> SUCCESS <<<");
					
					//---send ticket
					System.out.println("\t>>> TICKET : " + ticket);
					String sessionKey = ticket.split(",")[3];
					
					//---use webKey to encrypt---
					response = new String(Algor.encrypt(ticket, webKey)) + ",and," + sessionKey;
						
				}
				else{
					System.out.println("\t>>> FAILED  <<<");
					response = "[QUIT] cannot visit it.";
					flag = false;
				}
			}
		}
	////---end of analysis---

		//---send message (response) to client---
        System.out.println("Server[SEND]           : "+response);
        
        response = new String(Algor.encrypt(response, password));
        System.out.println("Server[SEND][ENCRYPT]  : "+response);
        System.out.println();
        
        pWriter.println(response);
        pWriter.flush();
        return flag;
	}
	
	public void start(){
        try{
        	PrintWriter    pWriter = new PrintWriter(socket.getOutputStream());
            BufferedReader bReader = new BufferedReader(
            		new InputStreamReader(socket.getInputStream()));
            
            while(reciMsg(socket,
            		      pWriter,
            		      bReader,
            		      target));
            pWriter.close();
            bReader.close();
            socket.close();
        }catch(Exception e) {e.printStackTrace();}
        finally{
            System.out.println("[QUIT] cannot find socket[ 'target' = '" + target+"' ]\n\n\n\n\n");
        }
    }
}
