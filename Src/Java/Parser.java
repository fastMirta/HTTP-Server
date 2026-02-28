package Src.Java;
import java.io.IOException;

import Src.Java.Helper.HTTP_METHODS;
import Src.Java.Utils.ResponseBuilder;

public class Parser {

    /**Parses the incoming HTTP request and validates it. If the request is valid, it will be handled accordingly. If the request is invalid, an appropriate error response will be sent back to the client.
     * 
     * @param request Full HTTP request string to parse and handle.
     * @throws IOException
     */
    public static void parseRequest(String request) throws IOException {
        if(!Helper.startsWithHTTPMethod(request)){
            //ResponseBuilder.sendResponse("HTTP/1.1 400 Bad Request\r\n\r\n Did not start with HTTP method");
            return;
        }

        else if(request == null || request.isEmpty()){
            //ResponseBuilder.sendResponse("HTTP/1.1 400 Bad Request\r\n\r\n Request holds no content");
            return;
        }

        String methodString = request.substring(0, request.indexOf("/") + 1);
        HTTP_METHODS method = Helper.getMethod(methodString);

        if(method == null){return;}

        switch (method) {
            case GET:
                int lastMethodCharIndex = request.indexOf("/");
                String path = request.substring(lastMethodCharIndex, request.indexOf("\\r\\n\\r\\n")).trim();
                parseGetRequest(path);
                break;
            case POST:
                break;
            case PUT:
                break;
            case DELETE:
                break;
            case PATCH:
                break;
        
            default:
                break;
        }

    }

    /**
     * 
     * @param request The HTTP GET request string to parse and handle. The request is AFTER the method, for example: GET /path HTTP/1.1 -> the request parameter would be "/path HTTP/1.1"
     *      
     */
    public static void parseGetRequest(String request)  {
        if(!Helper.isValidHTTPSection(request)){
            String response = "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\n" + Helper.getOutpotError();
            //ResponseBuilder.sendResponse(response);
            return;
        }

        else if(Helper.hasEcho(request)){
            String echoMessage = request.substring(request.indexOf("echo") + 4, request.indexOf("HTTP")).trim();
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\n" + echoMessage;
            //ResponseBuilder.sendResponse(response);
            return;
        }
        else if(!Helper.getOutpotError().equals("")){
            String response = "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\n" + Helper.getOutpotError();
            //ResponseBuilder.sendResponse(response);
            return;
        }
        else if(Helper.hasPath(request)){
            //TODO: Check if the path is valid. 
            String path = request.substring(request.indexOf("/") + 1, request.indexOf("HTTP")).trim();
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nYou requested the path: " + path;
            //ResponseBuilder.sendResponse(response);
            return;
        }
        else{
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, World!";
            //ResponseBuilder.sendResponse(response);
            return;
        }
    }

    public static void parsePostRequest(String request) throws IOException {
        if(!Helper.isValidHTTPSection(request)){
            String response = "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\n" + Helper.getOutpotError();
            //ResponseBuilder.sendResponse(response);
            return;
        }
    }
}
