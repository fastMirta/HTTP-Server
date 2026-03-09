package Src.Java;
import java.io.IOException;
import java.util.HashMap;

import Src.Java.Models.HttpRequest;
import Src.Java.Utils.Helper;
import Src.Java.Utils.Helper.HTTP_METHODS;

public class Parser {

    /**Parses the incoming HTTP request and validates it. If the request is valid, it will be handled accordingly. If the request is invalid, an appropriate error response will be sent back to the client.
     * 
     * @param request Full HTTP request string to parse and handle.
     * @throws IOException
     */
    public static HttpRequest parseRequest(String request) throws IOException {
        System.out.println("Parsing Request!!!");
        System.out.println("Request: " + request);
        if(request == null || request.isEmpty()){
            return null;
        }
        else if(!Helper.isValidHTTPSection(request)){
            return null;
        }
        System.out.println("Spliting initial request");
        String[] requestParts = request.split("\r\n");
        System.out.println("spliting again!");
        String[] requestContents = requestParts[0].split(" ");
        if(requestContents.length < 3){
            return null;
        }
        System.out.println("Getting method");
        HTTP_METHODS method = Helper.getMethod(requestContents[0]);

        if(method == null){return null;}
        
        System.out.println("Getting headers!!!");
        HashMap<String, String> headers = new HashMap<>();
        System.out.println("request parts length: " + requestParts.length);
        for(int i = 1; i < requestParts.length; i++){
            if(requestParts[i].isEmpty()){
                System.out.println("Empty");
                break;
            }
            String[] currentHeader = requestParts[i].split(": ", 2);
            System.out.println("current header: " + currentHeader[0] + " second: " + currentHeader[1]);
            headers.put(currentHeader[0], currentHeader[1]);
        }
        System.out.println("Done!!! Parsing Request!!!");
        System.out.println("method: " + method);
        System.out.println("path: " + requestContents[1]);
        System.out.println("headers: " + headers);
        System.out.println("length: " + requestParts.length);
        System.out.println("body: " + requestParts[requestParts.length - 1]);
        return new HttpRequest(method, requestContents[1], headers, requestParts[requestParts.length - 1]);
    }

}
