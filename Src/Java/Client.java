package Src.Java;

import java.io.BufferedOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    public Client(int port){
        try {
            socket = new Socket("localhost", port);
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            outputStream.write("GET / HTTP/1.1\r\n".getBytes());
            outputStream.flush();
            Thread.sleep(3000);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Client starting...");
        new Client(4221);
    }
}
