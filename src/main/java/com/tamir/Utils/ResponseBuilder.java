package com.tamir.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tamir.Main;
import com.tamir.Models.HttpResponse;

/** Class for building a response to the client */
public class ResponseBuilder {

    /** Logger to log valuable data */
    private static final Logger logger = LoggerFactory.getLogger(ResponseBuilder.class);

    /** Builds ResponseBuilder object */
    public ResponseBuilder() {
    }

    /**
     * Builds response to client based on HttpResponse
     * 
     * @param response Http Response
     * @return response to client for his request holds data such as status message
     *         and code
     */
    public String buildResponse(HttpResponse response) {
        logger.debug("Entering response builder");
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMessage() + "\r\n");
        responseBuilder.append("Content-Type: " + response.getContentType() + "\r\n");
        if (response.getBody() == null) {
            logger.debug("Body is null in buildResponse");
            responseBuilder.append("Content-Length: " + 0 + "\r\n");
            responseBuilder.append("\r\n");
            return responseBuilder.toString();
        }
        responseBuilder.append("Content-Length: " + response.getBody().getBytes().length + "\r\n");
        responseBuilder.append("\r\n");
        responseBuilder.append(response.getBody());
        return responseBuilder.toString();
    }

}