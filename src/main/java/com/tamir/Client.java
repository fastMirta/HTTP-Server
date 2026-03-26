package com.tamir;

import java.io.BufferedOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    public Client(int port){
        String req = " GET /test2.txt HTTP/1.1" +
"Host: localhost:4221" +
"User-Agent: curl/8.13.0" +
"Accept: */*";
        try {
            socket = new Socket("localhost", port);
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            outputStream.write(req.getBytes());
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
