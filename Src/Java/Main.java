package Src.Java;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Main{

    private final static int port = 4221;

    public static void main(String[] args) {
        System.out.println("Lets Start the Server...");
        try{
            ServerSocket server = new ServerSocket(port);
            System.out.println("server started");
            Socket accept = server.accept();
            System.out.println("client connected");
            OutputStream outputStream = accept.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(accept.getInputStream()));
            String input = bufferedReader.readLine(); 
            System.out.println("Received: " + input);
            if(Helper.startsWithHTTPMethod(input)){
                System.out.println("Received request: " + Helper.getCurrentMethod());
                String bodyParts = input.split("\r\n\r\n")[0];
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