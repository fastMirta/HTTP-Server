package com.tamir;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main{

    private final static int port = 4221;
    private final static String workingDirectory = System.getenv("WORKING_DIRECTORY");;
    private static AtomicInteger waiting = new AtomicInteger(0);
    private static AtomicInteger running = new AtomicInteger(0);
    private static String keysPw = System.getenv("KEYSTORE_PASSWORD");
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        logger.info("\n Lets Start the Server...");
        ServerSocket server = null;
        ExecutorService pool = Executors.newFixedThreadPool(2);
        if(keysPw == null){
            logger.error("KEYSTORE_PASSWORD env variable not set");
            return;
        }
        if(workingDirectory == null){
            logger.error("WORKING_DIRECTORY env variable isnt set");
            return;
        }
        try(FileInputStream keyStoreFile = new FileInputStream("HTTP-Server/keystore.p12")){
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreFile, keysPw.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keysPw.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
            server = sslContext.getServerSocketFactory().createServerSocket(port);
            logger.debug("server started");
            while(true){
                Socket accept = server.accept();
                logger.info("Cliend connected");
                
                ConnectionHandler connection = new ConnectionHandler(accept);
                
                waiting.incrementAndGet();
            
                pool.submit(() -> {
                    waiting.decrementAndGet();
                    running.incrementAndGet();
                    
                    System.out.println("waiting: " + waiting.get() + " running: " + running.get());
                    logger.debug("waiting: " + waiting.get() + " running: " + running.get());
                    
                    connection.run();
                    
                    running.decrementAndGet();
                });
            }
            
        }
        catch(KeyStoreException e){
            logger.error("Caught KeyStoreException: " + e.getMessage());
        }
        catch(Exception e){
            logger.error("Caught exception: " + e.getMessage());
        }
        finally{
            try {
                if(server != null){
                    logger.info("Closing server");
                    logger.debug("Closing server");
                    server.close();
                }
            } catch (IOException e) {
                logger.error("Caught IO exception in closing server: " + e.getMessage());
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