package Src.Java.Handler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import Src.Java.Main;
import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;
import Src.Java.Utils.Helper;

public class FileHandler implements Handler  {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String path = "";
        if(request != null && request.getPath() != null){
            path = Main.getWorkingDirectory() + request.getPath();
        }
        switch (request.getMethod()) {
            case GET:
                System.out.println("handle request: " + request.getPath());
                System.out.println("length: " + request.getPath().split("/").length);
                String[] filePath = request.getPath().split("/");
                String fileName = filePath[filePath.length - 1];
                System.out.println("File name: " + fileName);
                byte[] body = Helper.getFile(fileName);
                if(body != null){
                    return new HttpResponse(200, "OK", 
                 "text/plain", Arrays.toString(body));
                }
                return new HttpResponse(404, "Not found", "text/plain", Helper.getOutpotError());
            case POST:
                System.out.println("File handler post!!!");
                if(Helper.postFile(path, request.getBody().getBytes())){
                    System.out.println("POST ONCE AGAIN");
                    return new HttpResponse(201, "Created", "text/plain", request.getBody());
                }
                else if(Helper.getOutpotError().contains("File already exists")){
                    return new HttpResponse(409, "Conflict", "text/plain", Helper.getOutpotError());
                }
                return new HttpResponse(400, "Bad Request", "text/plain", Helper.getOutpotError());
            case PUT:
                if(Helper.putFile(path,
                 request.getBody().getBytes())){
                    return new HttpResponse(200, "OK", "text/plain", request.getBody());
                }
                return new HttpResponse(400, "Bad Request", "text/plain",Helper.getOutpotError());
            case DELETE:
                //TODO: implement a case for queue //status code 202
                if(Helper.deleteFile(path)){
                    return new HttpResponse(200, "OK",
                 "text/plain", request.getPath().substring(1));
                }
                return new HttpResponse(400, "Bad Request",
                 "text/plain", Helper.getOutpotError());
            case PATCH:
                if(Helper.patchFile(path,
                 request.getBody().getBytes())){
                    return new HttpResponse(206, "Partial Content", "text/plain", request.getBody());
                }
                return new HttpResponse(400, "Bad Request", "text/plain", Helper.getOutpotError());
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
