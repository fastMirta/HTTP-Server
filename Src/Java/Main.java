package Src.Java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main{

    private final static int port = 4221;
    private final static String workingDirectory = "F:/MyServer";

    public static void main(String[] args) {
        System.out.println("Lets Start the Server...");
        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
            System.out.println("server started");
            while(true){
                Socket accept = server.accept();
                System.out.println("client connected");
                
                ConnectionHandler connection = new ConnectionHandler(accept);
                new Thread(connection).start();
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                if(server != null){
                    server.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    
}