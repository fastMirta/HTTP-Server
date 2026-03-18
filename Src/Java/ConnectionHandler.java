package Src.Java;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import Src.Java.Handler.ErrorHandler;
import Src.Java.Handler.Handler;
import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;
import Src.Java.Utils.Helper;
import Src.Java.Utils.ResponseBuilder;

public class ConnectionHandler implements Runnable{
    Socket clientSocket;
    public ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        try {
            OutputStream clienOutputStream = clientSocket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = bufferedReader.readLine();
            StringBuilder input = new StringBuilder();
            System.out.println("B4 getting lines");
            String contentLength = "";
            while (line != null && !line.isEmpty()) {
                if(line.contains("Content-Length")){
                    contentLength = line;
                    System.out.println("line: " + line);
                }
                input.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            input.append("\r\n");

            if(contentLength.split(":").length == 2){
                
                int charactersToRead = Integer.parseInt(contentLength.split(":")[1].trim());
                //System.out.println("After getting them");
                char[] body = new char[charactersToRead];
                bufferedReader.read(body, 0, charactersToRead);
                input.append(body);
                System.out.println("body in start: " + Arrays.toString(body));
            }

            String clientInput = input.toString();
            
            HttpRequest request = Parser.parseRequest(clientInput);
            if(request == null){
                HttpResponse response = new HttpResponse(400, "Bad Request", "text/plain", Helper.getOutpotError());
                ResponseBuilder builder = new ResponseBuilder();
                clienOutputStream.write(builder.sendResponse(response).getBytes());
            }
            else{
                String[] requestParts = clientInput.split("\r\n");
                String[] requestContents = requestParts[0].split(" ");
                Handler handler = Router.routing(request);
                ResponseBuilder builder = new ResponseBuilder();
                if(handler instanceof ErrorHandler){
                    HttpResponse serverResponse = new HttpResponse(400, "Bad Request",
                 "text/plain", ((ErrorHandler) handler).getError());
                    clienOutputStream.write(builder.sendResponse(serverResponse).getBytes());
                }
                else{
                HttpResponse serverResponse = handler.handleRequest(request);
                
                //System.out.println("response: " + builder.sendResponse(serverRespone));
                clienOutputStream.write(builder.sendResponse(serverResponse).getBytes());
                }

            }

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            HttpResponse response = new HttpResponse(500, "Internal Server Error", "text/plain", null);
            ResponseBuilder builder = new ResponseBuilder();
            try {
                clientSocket.getOutputStream().write(builder.sendResponse(response).getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        

    }
    
}
