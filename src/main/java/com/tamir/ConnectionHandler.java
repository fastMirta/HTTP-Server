package com.tamir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.tamir.Handler.ErrorHandler;
import com.tamir.Handler.Handler;
import com.tamir.Models.HttpRequest;
import com.tamir.Models.HttpResponse;
import com.tamir.Utils.ResponseBuilder;
import com.tamir.Utils.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//src.main.java.com.tamir.
public class ConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    Socket clientSocket;

    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        boolean isConnectionClosed = false;
        try {
            OutputStream clienOutputStream = clientSocket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String line;
            StringBuilder input;
            String contentLength;


            while (!isConnectionClosed) {
                try {
                    line = bufferedReader.readLine();
                    input = new StringBuilder();
                    contentLength = "";
                    // Thread.sleep(3000);
                    if(line == null){
                        isConnectionClosed = true;
                        break;
                    }

                    while (line != null && !line.isEmpty()) {
                        if (line.contains("Content-Length")) {
                            contentLength = line;
                        }
                        input.append(line).append("\r\n");
                        line = bufferedReader.readLine();
                    }
                    input.append("\r\n");

                    if (contentLength.split(":").length == 2) {

                        int charactersToRead = Integer.parseInt(contentLength.split(":")[1].trim());
                        // System.out.println("After getting them");
                        char[] body = new char[charactersToRead];
                        bufferedReader.read(body, 0, charactersToRead);
                        input.append(body);
                    }

                    String clientInput = input.toString();
                    logger.debug(clientInput);

                    Result<HttpRequest> request = Parser.parseRequest(clientInput);
                    if (!request.isSuccess()) {
                        HttpResponse response = new HttpResponse(400, "Bad Request", "text/plain", request.getError());
                        ResponseBuilder builder = new ResponseBuilder();
                        String serversResponse = builder.buildResponse(response);
                        clienOutputStream.write(serversResponse.getBytes());
                    } else {
                        // String[] requestParts = clientInput.split("\r\n");
                        // String[] requestContents = requestParts[0].split(" ");
                        Handler handler = Router.routing(request.getData());
                        ResponseBuilder builder = new ResponseBuilder();
                        if (handler instanceof ErrorHandler) {
                            HttpResponse serverResponse = new HttpResponse(400, "Bad Request",
                                    "text/plain", ((ErrorHandler) handler).getError());
                            clienOutputStream.write(builder.buildResponse(serverResponse).getBytes());
                        } else {
                            HttpResponse serverResponse = handler.handleRequest(request.getData());

                            // System.out.println("response: " + builder.sendResponse(serverRespone));
                            clienOutputStream.write(builder.buildResponse(serverResponse).getBytes());
                        }

                    }
                    isConnectionClosed = isConnectionClosed(clientInput);

                } catch (IOException e) {
                    logger.error("Caught IO exception in ConnectionHandler", e);
                    logger.debug("error: " + e.getMessage());
                    HttpResponse response = new HttpResponse(500, "Internal Server Error", "text/plain", null);
                    ResponseBuilder builder = new ResponseBuilder();
                    try {
                        clientSocket.getOutputStream().write(builder.buildResponse(response).getBytes());
                        isConnectionClosed = true;
                        logger.info("Closed connection");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        logger.error("Caught IO exception while trying to write a response");
                        isConnectionClosed = true;
                        logger.info("Closed connection");
                    }
                    // } catch (InterruptedException e) {
                    // logger.error("Caught Interrupted exception: " + e.getMessage());
                    // e.printStackTrace();
                }

            }
        } catch (Exception e) {
            logger.error("Caught exception: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("Caught IO exception while closing client");
                e.printStackTrace();
            }
        }
    }

    private boolean isConnectionClosed(String request) {
        String[] headAndLine = request.split("\r\n\r\n");
        String[] headers = headAndLine[0].split("\r\n");
        for (int i = 1; i < headers.length; i++) {
            if (headers[i].contains("Connection:")) {
                String connectionState = headers[i].substring(headers[i].indexOf("Connection:") + 11);
                return connectionState.equals("close");
            }
        }
        return false;
    }

}