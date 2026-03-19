package com.tamir.Handler;

import java.nio.charset.StandardCharsets;

import com.tamir.Main;
import com.tamir.Models.HttpRequest;
import com.tamir.Models.HttpResponse;
import com.tamir.Utils.Helper;
import com.tamir.Utils.Result;

public class FileHandler implements Handler  {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String path = "";
        if(request != null && request.getPath() != null){
            path = Main.getWorkingDirectory() + request.getPath();
        }
        switch (request.getMethod()) {
            case GET:
                Result<byte[]> getResult = Helper.getFile(path); 
                //byte[] body = 
                if(getResult.isSuccess()){
                    return new HttpResponse(200, "OK", 
                 "text/plain", new String(getResult.getData(), StandardCharsets.UTF_8));
                }
                return new HttpResponse(404, "Not found", "text/plain", getResult.getError());
            case POST:
                System.out.println("File handler post!!!");
                Result<Void> postResult = Helper.postFile(path, request.getBody().getBytes()); 
                if(postResult.isSuccess()){
                    System.out.println("POST ONCE AGAIN");
                    return new HttpResponse(201, "Created", "text/plain", request.getBody());
                }
                else if(postResult.getError().contains("File already exists")){
                    return new HttpResponse(409, "Conflict", "text/plain", postResult.getError());
                }
                return new HttpResponse(400, "Bad Request", "text/plain", postResult.getError());
            case PUT:
                Result<Void> putResult = Helper.putFile(path,
                 request.getBody().getBytes());
                if(putResult.isSuccess()){
                    return new HttpResponse(200, "OK", "text/plain", request.getBody());
                }
                return new HttpResponse(400, "Bad Request", "text/plain", putResult.getError());
            case DELETE:
                //TODO: implement a case for queue //status code 202
                Result<Void> deleteResult = Helper.deleteFile(path);
                if(deleteResult.isSuccess()){
                    return new HttpResponse(200, "OK",
                 "text/plain", request.getPath().substring(1));
                }
                return new HttpResponse(400, "Bad Request",
                 "text/plain", deleteResult.getError());
            case PATCH:
                Result<Void> patchResult = Helper.patchFile(path,
                 request.getBody().getBytes());
                if(patchResult.isSuccess()){
                    return new HttpResponse(206, "Partial Content", "text/plain", request.getBody());
                }
                return new HttpResponse(400, "Bad Request", "text/plain", patchResult.getError());
            default:
                return new HttpResponse(405, "Method Not Allowed", "text/plain", "The requested method is not supported.");
                
            
        }
    }

    
}

/**
 * Get: GET /{} HTTP/1.1 /r/n/r/n
 * /r/n/r/n
 * body
 * 
 * 
 * 
 */
