package Src.Java.Utils;

import java.net.Socket;

//TODO This class will be responsible for building the response to be sent to the client. It will take care of setting the appropriate headers, status codes, and body content based on the request and the server's logic. This will help in keeping the code organized and maintainable as the server grows in complexity.
public class ResponseBuilder {
    static Socket clientSocket;

    // public static void sendResponse(String response) {
    //     try {
    //         clientSocket.getOutputStream().write(response.getBytes());
    //         clientSocket.getOutputStream().flush();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}