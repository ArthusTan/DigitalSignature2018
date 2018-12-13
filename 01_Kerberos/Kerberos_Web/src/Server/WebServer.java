package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    public static void main(String[] args) throws Exception{
        try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(7777);
            System.out.println("--- WEB SERVER ---");
            
            //同时建立多个线程对不同socket对象进行处理（长连接）
            while (true) {
                Socket socket = serverSocket.accept();
                //将新的socket对象放入线程
                WebSocketThread socketThread = new WebSocketThread(socket);
                
                socketThread.start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}