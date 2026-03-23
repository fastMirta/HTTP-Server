package com.tamir;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tamir.Models.HttpRequest;
import com.tamir.Utils.Helper;
import com.tamir.Utils.Helper.HTTP_METHODS;
import com.tamir.Utils.Result;

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    /**Parses the incoming HTTP request and validates it. If the request is valid, it will be handled accordingly. If the request is invalid, an appropriate error response will be sent back to the client.
     * 
     * @param request Full HTTP request string to parse and handle.
     * @throws IOException
     */
    public static Result<HttpRequest> parseRequest(String request) throws IOException {
        logger.info("Started Parsing");
        logger.debug("Parsing request: " + request);
        if(request == null || request.isEmpty()){
            logger.error("request is null or empty");
            return Result.failure("request is null or empty");
        }
        Result<Void> httpSecValidationResult = Helper.isValidHTTPSection(request);
        if(!httpSecValidationResult.isSuccess()){
            return Result.failure(httpSecValidationResult.getError());
        }
        logger.debug("Spliting initial request");
        String[] requestParts = request.split("\r\n\r\n"); //requestParts[0] = GET /{path} HTTP/1.1 + headers, requestParts[1] = body
        logger.debug("spliting again!");
        String[] requestContents = requestParts[0].split("\r\n"); //requestContents[0] = GET /{path} HTTP/1.1, requestContents[1...n] = headers
        String[] requestLine = requestContents[0].split(" ");
        String path = requestLine[1];
        // if(requestContents.length < 3){
        //     logger.error("RequestContents length less than three");
        //     return Result.failure("RequestContents length less than three");
        // }
        logger.debug("Getting method");
        logger.debug("Request contents index 0: " + requestLine[0]);
        HTTP_METHODS method = Helper.getMethod(requestLine[0]);
        logger.debug("Method: " + method);

        if(method == null){return Result.failure("Could not find supported http method");}
        
        logger.info("Getting headers!!!");
        HashMap<String, String> headers = new HashMap<>();
        logger.debug("request parts length: " + requestParts.length);
        for(int i = 1; i < requestContents.length; i++){
            if(requestContents[i].isEmpty()){
                logger.debug("Empty");
                break;
            }
            String[] currentHeader = requestContents[i].split(": ", 2);
            logger.debug("current header: " + currentHeader[0] + " second: " + currentHeader[1]);
            headers.put(currentHeader[0], currentHeader[1]);
        }

        logger.debug("Done!!! Parsing Request!!!");
        logger.debug("method: " + method);
        logger.debug("path: " + path);
        logger.debug("headers: " + headers);
        logger.debug("length: " + requestContents.length);
        logger.debug("body: " + requestParts[requestParts.length - 1]);
        return Result.success(new HttpRequest(method, path, headers, requestParts[requestParts.length - 1]));
    }

}
