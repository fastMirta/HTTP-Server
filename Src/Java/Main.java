package Src.Java;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Main{

    private final static int port = 4221;
    private final static String workingDirectory = "F:/MyServer";

    public static void main(String[] args) {
        System.out.println("Lets Start the Server...");
        // if(!Helper.hasSpaceBeforeHTTPVersion("GET / a HTTP/1.1")){
        //     System.out.println("Invalid HTTP version");
        //     return;
        // }
        System.out.println("HTTP version is valid");
        try{
            ServerSocket server = new ServerSocket(port);
            System.out.println("server started");
            Socket accept = server.accept();
            System.out.println("client connected");
            OutputStream outputStream = accept.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(accept.getInputStream()));
            String input = bufferedReader.readLine(); 
            System.out.println("Received: " + input);
            StringBuilder readLine = new StringBuilder(bufferedReader.readLine());

            while(readLine != null && !readLine.isEmpty()){
                if(readLine != null){
                    input += "\r\n" + readLine;
                }
                readLine.append(bufferedReader.readLine());
            }

            String[] requestLines = input.split("\r\n");
            if(Helper.startsWithHTTPMethod(requestLines[0])){
                System.out.println("Received request: " + Helper.getCurrentMethod());
                String bodyParts = input.split("\r\n\r\n")[0];
                //Helper.handleRequest(input, outputStream);
            }
            else{
                outputStream.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
            }
            
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    
}