package Src.Java.Models;

import java.util.HashMap;

import Src.Java.Helper.HTTP_METHODS;

public class HttpRequest {
    private HTTP_METHODS method;
    private String path;
    private HashMap<String, String> headers;
    private String body;

    public HttpRequest(HTTP_METHODS method, String path, HashMap<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    //=== Getters and Setters ===//
    public HTTP_METHODS getMethod() {
        return method;
    }

    public void setMethod(HTTP_METHODS method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
}