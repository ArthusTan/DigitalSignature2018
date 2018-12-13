package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer { // Reuse Server.java
	private static String userName = "user_00";
	private static String password = "0000000";
		// Here we use user_00 for default web server.
		// If not, please change the value here.
		// Please keep it correct!
	
	public static void main(String[] args) throws Exception{
    	@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(7777); // KPort = 7777
	    System.out.println("--- WEB SERVER ---");
	    while (true) { // Several Threads for Different Socket Objects
	        Socket socket = serverSocket.accept();
	        WebSocketThread socketThread = new WebSocketThread(socket, userName, password); //New Thread
	        socketThread.start(); // Start the Thread
	    }
    }
}
