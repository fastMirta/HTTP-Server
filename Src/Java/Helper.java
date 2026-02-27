package Src.Java;

import java.io.IOException;
import java.io.OutputStream;

public class Helper {
    public enum HTTP_METHODS {
        GET, POST, PUT, DELETE, PATCH
    }

    public static HTTP_METHODS currentMethod;

    public static boolean hasEcho(String request) {
        if(request == null){
            return false;
        }
        else if(request.length() < 4){
            return false;
        }
        for(int i = 0; i < request.length() - 3; i++){
            if(request.substring(i, i + 4).equals("echo")){
                return true;
            }
        }
        return false;
    }

    public static HTTP_METHODS getMethod(String request) {
        if(request == null){
            return null;
        }
        switch (request) {
            case "GET":
                currentMethod = HTTP_METHODS.GET;
                return HTTP_METHODS.GET;
            case "POST":
                currentMethod = HTTP_METHODS.POST;
                return HTTP_METHODS.POST;
            case "PUT":
                currentMethod = HTTP_METHODS.PUT;
                return HTTP_METHODS.PUT;
            case "DELETE":
                currentMethod = HTTP_METHODS.DELETE;
                return HTTP_METHODS.DELETE;
            case "PATCH":
                currentMethod = HTTP_METHODS.PATCH;
                return HTTP_METHODS.PATCH;
            default:
                return null;
        }
    }

    public static HTTP_METHODS getCurrentMethod() {
        return currentMethod;
    }

    public static void handleRequest(String request, OutputStream outputStream) {
        if(request == null){
            return;
        }
        else if(!startsWithHTTPMethod(request)){
            try{
                outputStream.write("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nBad Request".getBytes());
                outputStream.flush();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return;
        }
        
        switch (currentMethod) {
            case GET:
                System.out.println("GET request received");
                boolean success = handleGetMethod(request, outputStream);
                break;
            case POST:
                System.out.println("POST request received");
                break;
            case PUT:
                System.out.println("PUT request received");
                break;
            case DELETE:
                System.out.println("DELETE request received");
                break;
            case PATCH:
                System.out.println("PATCH request received");
                break;
            default:
                break;
        }
    }

    public static boolean handleGetMethod(String request, OutputStream outputStream){
        System.out.println("outputStream null? " + (outputStream == null));
        try{
            
            if(request == null){
                outputStream.write("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nBad Request".getBytes());
                outputStream.flush();
                return false;
            }
            else if(hasEcho(request)){

            }
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, World!";
            outputStream.write(response.getBytes());
            outputStream.flush();
            return true;
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }


    public static boolean startsWithHTTPMethod(String request) {
        if(request == null){
            return false;
        }
        String req = request.substring(0, request.indexOf("/"));
        if(getMethod(req) != null){
            return true;
        }
        return false;
    }

    public static boolean isValidHTTPVersion(String request) {
        if(request == null){
            return false;
        }
        String version = request.substring(request.indexOf("HTTP/"), request.indexOf("\r\n"));
        if(version.equals("HTTP/1.1")){
            return true;
        }
        return false;
    }

}

