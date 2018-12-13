package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Algorithm.Algor;

public class WebSocketThread extends Thread{
	private Socket socket = null;
	private String target = "";
	private static String password = "0000000";
	private static String sessionKey = "";
	@SuppressWarnings("unused")
	private static String userName = "user_00";
	
	public WebSocketThread(Socket socket){
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
			System.out.println("Client  : " + message);
		} catch (Exception e) { return false; }
        
		String response = "get message from " + target;///normal response
		
		////---begin to analysis : different functions (后期可函数化)---
		
		response = new String(Algor.decipher(message, password));
		System.out.println("[TICKET],SK   = " + response);
		String[] array = response.split(",");
		if(array[0].equals("[TICKET]")){
			sessionKey = array[3];
			System.out.println("[SESSION KEY] = " + sessionKey);
			response = new String(Algor.encrypt(response, sessionKey));
		}
		else{
			response = new String("[QUIT] WRONG TICKET");
		}
		////---end analysis---

        System.out.println("Server  : "+response);
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
