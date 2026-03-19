package com.tamir.Handler;

import com.tamir.Models.HttpRequest;
import com.tamir.Models.HttpResponse;


public class EchoHandler implements Handler {
    
    @Override
    public HttpResponse handleRequest(HttpRequest request) {
    //TODO: implement logic for other methods
        switch (request.getMethod()) {
            case GET:
                return new HttpResponse(200, "OK",
                 "text/plain", request.getPath().substring(6));
            case POST:
                return new HttpResponse(200, "OK", "text/plain", request.getBody());
            case PUT:
                return new HttpResponse(200, "OK", "text/plain", request.getBody());
            case DELETE:
                return new HttpResponse(200, "OK", 
                 "text/plain", request.getPath().substring(6, request.getPath().length()));
            case PATCH:
                return new HttpResponse(200, "OK", "text/plain", request.getBody());
            default:
                return null;
        }
    }

    
    
}
