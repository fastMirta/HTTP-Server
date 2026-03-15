package Src.Java.Utils;

import java.net.Socket;

import Src.Java.Models.HttpResponse;


//TODO This class will be responsible for building the response to be sent to the client. It will take care of setting the appropriate headers, status codes, and body content based on the request and the server's logic. This will help in keeping the code organized and maintainable as the server grows in complexity.
public class ResponseBuilder {


    public ResponseBuilder(){}

    public String sendResponse(HttpResponse response){
        if(response == null || response.getBody() == null){
            return null;
        }
        StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMessage() + "\r\n");
            responseBuilder.append("Content-Type: " + response.getContentType() + "\r\n");
            responseBuilder.append("Content-Length: " + response.getBody().getBytes().length + "\r\n");
            responseBuilder.append("\r\n");
            responseBuilder.append(response.getBody());
        return responseBuilder.toString();
    }
    

}