package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception{
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(8888); // KPort = 8888
        System.out.println("--- SERVER ---");
        while (true) { // Several Threads for Different Socket Objects
            Socket socket = serverSocket.accept();
            SocketThread socketThread = new SocketThread(socket); //New Thread
            socketThread.start(); // Start the Thread
        }
    }
}
