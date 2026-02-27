package Src.Java;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Main{

    private final static int port = 4221;

    public static void main(String[] args) {
        try{
            ServerSocket server = new ServerSocket(port);
            Socket accept = server.accept();
            
            OutputStream outputStream = accept.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(accept.getInputStream()));
            String input = bufferedReader.readLine();
            if(Helper.startsWithHTTPMethod(input)){
                String bodyParts = input.split("\r\n\r\n")[1];
                Helper.handleRequest(input, outputStream);
            }
            else{
                outputStream.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
            }
            
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    
}