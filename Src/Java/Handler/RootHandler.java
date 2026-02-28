package Src.Java.Handler;


import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;

public class RootHandler implements Handler {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        switch (request.getMethod()) {
            case GET:
                return new HttpResponse(200, "OK", "text/plain", "Hello, World!");
            case POST:
                return new HttpResponse(201, "Created", "text/plain", "Resource created successfully.");
            case PUT:
                return new HttpResponse(200, "OK", "text/plain", "Resource updated successfully.");
            case DELETE:
                return new HttpResponse(200, "OK", "text/plain", "Resource deleted successfully.");
            case PATCH:
                return new HttpResponse(200, "OK", "text/plain", "Resource patched successfully.");
            default:
                return new HttpResponse(405, "Method Not Allowed", "text/plain", "The requested method is not supported.");
        }
    }
    
}
