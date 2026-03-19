package com.tamir;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.tamir.Handler.ErrorHandler;
import com.tamir.Handler.Handler;
import com.tamir.Models.HttpRequest;
import com.tamir.Models.HttpResponse;
import com.tamir.Utils.Helper;
import com.tamir.Utils.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//src.main.java.com.tamir.
public class ConnectionHandler implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    Socket clientSocket;
    public ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    //curl http://localhost:4221/test/HTTP/1.1/test.txt
    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            OutputStream clienOutputStream = clientSocket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = bufferedReader.readLine();
            StringBuilder input = new StringBuilder();
            String contentLength = "";
            while (line != null && !line.isEmpty()) {
                if(line.contains("Content-Length")){
                    contentLength = line;
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
            }

            String clientInput = input.toString();
            
            HttpRequest request = Parser.parseRequest(clientInput);
            if(request == null){
                HttpResponse response = new HttpResponse(400, "Bad Request", "text/plain", Helper.getOutpotError());
                ResponseBuilder builder = new ResponseBuilder();
                String serversResponse = builder.sendResponse(response);
                if(serversResponse == null){
                    StringBuilder responseBuilder = new StringBuilder();
                    responseBuilder.append("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMessage() + "\r\n");
                    responseBuilder.append("Content-Type: " + response.getContentType() + "\r\n");
                    responseBuilder.append("Content-Length: " + response.getBody().getBytes().length + "\r\n");
                    responseBuilder.append("\r\n");
                    responseBuilder.append(response.getBody());
                    clienOutputStream.write(responseBuilder.toString().getBytes());
                    
                    return;
                }
                clienOutputStream.write(serversResponse.getBytes());
            }
            else{
                // String[] requestParts = clientInput.split("\r\n");
                // String[] requestContents = requestParts[0].split(" ");
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
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
