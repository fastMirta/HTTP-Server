package com.tamir.Handler;


import com.tamir.Models.HttpRequest;
import com.tamir.Models.HttpResponse;

public class RootHandler implements Handler {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        switch (request.getMethod()) {
            case GET:
                System.out.println("in get");
                return new HttpResponse(200, "OK", "text/plain", "Hello, World!");
            case POST:
                System.out.println("in post");
                return new HttpResponse(201, "Created", "text/plain", "Resource created successfully.");
            case PUT:
                System.out.println("in put");
                return new HttpResponse(200, "OK", "text/plain", "Resource updated successfully.");
            case DELETE:
                System.out.println("in delete");
                return new HttpResponse(200, "OK", "text/plain", "Resource deleted successfully.");
            case PATCH:
                System.out.println("in patch");
                return new HttpResponse(200, "OK", "text/plain", "Resource patched successfully.");
            default:
                System.out.println("not allowed");
                return new HttpResponse(405, "Method Not Allowed", "text/plain", "The requested method is not supported.");
                //maybe change to null
        }
    }
    
}
