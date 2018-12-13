package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception{
        try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("--- SERVER ---");
            
            //同时建立多个线程对不同socket对象进行处理（长连接）
            while (true) {
                Socket socket = serverSocket.accept();
                //将新的socket对象放入线程
                SocketThread socketThread = new SocketThread(socket);
                
                socketThread.start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}