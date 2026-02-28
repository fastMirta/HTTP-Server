package Src.Java.Handler;

import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;

public class EchoHandler implements Handler {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
    //TODO: implement logic for other methods
        switch (request.getMethod()) {
            case GET:
                return new HttpResponse(200, "OK", "text/plain", request.getPath());
            case POST:
                return null;
            case PUT:
                return null;
            case DELETE:
                return null;
            case PATCH:
                return null;
            default:
                return null;
        }
    }
    
}
