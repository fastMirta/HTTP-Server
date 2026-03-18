package Src.Java.Handler;

import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;

public class ErrorHandler implements Handler {

    private String error = "";

    public ErrorHandler(String error){
        this.error = error;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        return null;
    }

    public String getError(){return error;}
    
}
