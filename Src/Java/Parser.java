package Src.Java;
import java.io.IOException;
import java.util.HashMap;

import Src.Java.Models.HttpRequest;
import Src.Java.Utils.Helper;
import Src.Java.Utils.ResponseBuilder;
import Src.Java.Utils.Helper.HTTP_METHODS;

public class Parser {

    /**Parses the incoming HTTP request and validates it. If the request is valid, it will be handled accordingly. If the request is invalid, an appropriate error response will be sent back to the client.
     * 
     * @param request Full HTTP request string to parse and handle.
     * @throws IOException
     */
    public static HttpRequest parseRequest(String request) throws IOException {
        if(request == null || request.isEmpty()){
            return null;
        }
        else if(!Helper.isValidHTTPSection(request)){
            return null;
        }
        
        String[] requestParts = request.split("\r\n");
        String[] requestContents = requestParts[0].split(" ");
        if(requestContents.length < 3){
            return  null;
        }
        HTTP_METHODS method = Helper.getMethod(requestContents[0]);

        if(method == null){return null;}

        HashMap<String, String> headers = new HashMap<>();
        for(int i = 1; i < requestParts.length; i++){
            if(requestParts[i].isEmpty()){
                break;
            }
            String[] currentHeader = requestParts[i].split(": ", 2);
            headers.put(currentHeader[0], currentHeader[1]);
        }

        return new HttpRequest(method, requestContents[1], headers, requestParts[requestParts.length - 1]);
    }

}
