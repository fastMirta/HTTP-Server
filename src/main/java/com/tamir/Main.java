package com.tamir;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main{

    private final static int port = 4221;
    private final static String workingDirectory = "F:/MyServer";
    private static AtomicInteger waiting = new AtomicInteger(0);
    private static AtomicInteger running = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        System.out.println("Lets Start the Server...");
        ServerSocket server = null;
        ExecutorService pool = Executors.newFixedThreadPool(2);
        
        try{
            server = new ServerSocket(port);
            System.out.println("server started");
            while(true){
                Socket accept = server.accept();
                logger.info("Cliend connected");
                
                ConnectionHandler connection = new ConnectionHandler(accept);
                
                waiting.incrementAndGet();
            
                pool.submit(() -> {
                    waiting.decrementAndGet();
                    running.incrementAndGet();
                    
                    System.out.println("waiting: " + waiting.get() + " running: " + running.get());
                    
                    connection.run();
                    
                    running.decrementAndGet();
                });
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
                e.printStackTrace();
            }
        }
    }

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    public static AtomicInteger getWaiting(){return waiting;}

    public static AtomicInteger getRunning(){return running;}

    
}