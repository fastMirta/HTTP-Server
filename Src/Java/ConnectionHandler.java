package Src.Java;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import Src.Java.Handler.Handler;
import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;
import Src.Java.Utils.Helper;
import Src.Java.Utils.ResponseBuilder;

public class ConnectionHandler implements Runnable{
    Socket clientSocket;
    public ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            OutputStream clienOutputStream = clientSocket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = bufferedReader.readLine();
            StringBuilder input = new StringBuilder();
            System.out.println("B4 getting lines");
            while (line != null && !line.isEmpty() ) {
                input.append(line);
                line = bufferedReader.readLine();
            }
            System.out.println("After getting them");
            String clientInput = input.toString();
            HttpRequest request = Parser.parseRequest(clientInput);
            if(request == null){
                HttpResponse response = new HttpResponse(400, "Bad Request", "text/plain", Helper.getOutpotError());
                ResponseBuilder builder = new ResponseBuilder();
                clienOutputStream.write(builder.sendResponse(response).getBytes());
            }
            else{
                Handler router = Router.routing(request);
                HttpResponse serverRespone = router.handleRequest(request);
                ResponseBuilder builder = new ResponseBuilder();
                //System.out.println("response: " + builder.sendResponse(serverRespone));
                clienOutputStream.write(builder.sendResponse(serverRespone).getBytes());
            }

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            HttpResponse response = new HttpResponse(500, "Internal Server Error", "text/plain", null);
            ResponseBuilder builder = new ResponseBuilder();
            try {
                clientSocket.getOutputStream().write(builder.sendResponse(response).getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        

    }
    
}
