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
	private static String ServerKey = "";
	private static String password = "";
	@SuppressWarnings("unused")
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
        
		String response = "get message from " + target;///normal response
		
		////---begin to analysis : different functions (后期可函数化)---
		
		//---login---
		if(message.startsWith("login")){
			System.out.println("\t>>> LOGIN   <<<");
			if(AServer.confirm(message, 1, 1) >= 0){
				System.out.println("\t>>> SUCCESS <<<");
				String[] array = message.split(",");
				userName = array[1];
				password = array[2];
				
				response = "Login successful.";
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
				if(AServer.visit(message)){
					//---get webKey---
					webKey = AServer.visitKey(message);
					System.out.println("\t>>> GET WEBKEY : "+webKey);
					System.out.println("\t>>> SUCCESS <<<");
					String[] array = message.split(",");
					String TGT = "[ALLOW],"+array[1]+","+array[2];
					//response = "[ALLOW]," + new String(Algor.encrypt(TGT, ServerKey));
					response = new String(Algor.encrypt(TGT, ServerKey));
				}
				else{
					System.out.println("\t>>> FAILED  <<<");
					response = "[QUIT] Donot have enough power to visit it.";
					flag = false;
				}
			}
			else{
			
				//---ask ticket
				if(message.startsWith("askTicket")){
					System.out.println("\t>>> TICKET  <<<");
					String TGT = message.substring("askTicket,".length(), message.length());
					System.out.println("TGT     >>> " + TGT);
					TGT = new String(Algor.decipher(TGT, ServerKey));
					System.out.println("TGT     >>> " + TGT);
					String ticket = TGServer.getTicket(TGT);
					
					if(ticket.startsWith("[ALLOW]")){
						System.out.println("\t>>> SUCCESS <<<");
						response = ticket.replaceFirst("ALLOW", "TICKET") + "," + "[SESSION KEY]" + "," + "[TIMESTAMP]";
						System.out.println("[Ticket] >>> " + response);
						//---use webKey to encrypt---
						response = new String(Algor.encrypt(response, webKey)) + ",and," + "[SESSION KEY]";
					}
					else{
						System.out.println("\t>>> FAILED  <<<");
						response = "[QUIT] TGT is illegal.";
						flag = false;
					}
				}
			}
		}
		////---end analysis---

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
        }catch(Exception e) {e.printStackTrace();;}
        finally{
            System.out.println("[QUIT] cannot find socket[ 'target' = '" + target+"' ]\n\n\n\n\n");
        }
    }
}
