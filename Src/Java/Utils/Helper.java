package Src.Java.Utils;


import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import Src.Java.Main;

public class Helper {
    public enum HTTP_METHODS {
        GET, POST, PUT, DELETE, PATCH
    }
    private static String outpotError = "";
    private static final long maxSize = 10L * 1024 * 1024 * 1024;

    public static HTTP_METHODS currentMethod;

    public static boolean hasEcho(String request) {
        if(request == null){
            return false;
        }
        else if(request.length() < 7){
            return false;
        }
        String echoString = request.substring(0,  6);
        String echoMesage = request.substring(6);
        if(!echoString.equals("/echo/") || echoMesage.isEmpty() || echoMesage.contains(" ")){
            outpotError = "Invalid echo parameter";
            return false;
        }
        return true;
    }

    /**Validates that the request contains a path after the HTTP method and before the HTTP version. For example, in "GET /path HTTP/1.1", the path is "/path". The method checks that there is a non-empty path and that it does not contain invalid characters or multiple spaces.
     * 
     * @param request The HTTP request string to validate. This should be the part of the request line after the HTTP method, for example: "/path HTTP/1.1".
     * @return true if the request contains a valid path, false otherwise. If the path is invalid, an appropriate error message is set in the outpotError variable.
     */
    public static boolean hasPath(String path) {
        if(path == null){
            return false;
        }
        //boolean containsLetters = path.toLowerCase().contains("abcdefghijklmnopqrstuvwxyz");
        System.out.println("PATH: " + path);
        //System.out.println("contains letter: " + containsLetters);
        System.out.println("isnt empty: " + !path.isEmpty());
        System.out.println("doesnt contains space?: " + !path.contains(" "));
        return (!path.isEmpty() && !path.contains(" ") && !path.equals("/"));
    }

    public static HTTP_METHODS getMethod(String request) {
        if(request == null){
            return null;
        }
        switch (request) {
            case "GET":
                currentMethod = HTTP_METHODS.GET;
                return HTTP_METHODS.GET;
            case "POST":
                currentMethod = HTTP_METHODS.POST;
                return HTTP_METHODS.POST;
            case "PUT":
                currentMethod = HTTP_METHODS.PUT;
                return HTTP_METHODS.PUT;
            case "DELETE":
                currentMethod = HTTP_METHODS.DELETE;
                return HTTP_METHODS.DELETE;
            case "PATCH":
                currentMethod = HTTP_METHODS.PATCH;
                return HTTP_METHODS.PATCH;
            default:
                return null;
        }
    }

    public static HTTP_METHODS getCurrentMethod() {
        return currentMethod;
    }






    /**Validates that there is exactly one space before the HTTP version in the request line. For example,
     * "GET / HTTP/1.1" is valid, but "GET /HTTP/1.1" and "GET /  HTTP/1.1" are not.
     * 
     * @param request The HTTP request string to validate.
     * @return true if there is exactly one space before the HTTP version, false otherwise.
     */
    // public static boolean hasSpaceBeforeHTTPVersion(String request) {//GET / a HTTP/1.1
    //     if(request == null){
    //         return false;
    //     }
    //     String ver = request.substring(0, request.indexOf("HTTP/"));
    //     boolean hasSpace = ver.charAt(request.indexOf("HTTP/") - 1) == ' ';
    //     boolean hasTwoSpaces = ver.charAt(request.indexOf("HTTP/") - 2) == ' ';
    //     return (hasSpace && !hasTwoSpaces);
    // }

    public static boolean isValidHTTPSection(String request) {
        System.out.println("validating http");
        if(!isValidHTTPVersion(request)){
            System.out.println("Not Valid");
            //outpotError = "HTTP version not found";
            return false;
        }
        else if(hasMoreThanOneSpace(request)){
            System.out.println(request);
            System.out.println("No space valid");
            outpotError = "More than one space found";
            return false;
        }
        System.out.println("DONE!!! validating http");
        return true;
    }

    public static boolean hasMoreThanOneSpace(String request) {
        int counter = 0;
        for(int i = 0; i < request.length(); i++){
            if(request.charAt(i) == ' '){
                counter++;
            }
            else{
                break;
            }
        }
        if(request == null || counter == 0){
            return false;
        }
        return counter > 1;
    }

    /**Validates that the request starts with a valid HTTP method (GET, POST, PUT, DELETE, PATCH).
     * 
     * @param request The HTTP request string to validate.
     * @return true if the request starts with a valid HTTP method, false otherwise.
     */
    public static boolean startsWithHTTPMethod(String request) {
        String req = request.substring(0, request.indexOf("/")).trim();
        if(getMethod(req) != null){
            System.out.println("Request starts with HTTP method: " + req);
            return true;
        }
        System.out.println("Request does not start with HTTP method: " + req);
        return false;
    }

    /**Validates that the HTTP version in the request is correct. Currently only supports HTTP/1.1
     * 
     * @param request The HTTP request string to validate.
     * @return true if the HTTP version is valid, false otherwise.
     */
    public static boolean isValidHTTPVersion(String request) {
        if(request == null){
            outpotError = "Didnt find HTTP version";
            return false;
        }
        
        else if(request.indexOf("HTTP/1.1") == -1){
            outpotError = "HTTP version not found";
            return false;
        }
        System.out.println("Extracting version");
        String httpSection = request.substring(request.indexOf("HTTP/1.1"));
        System.out.println("last index: " + (httpSection.length() - 1));
        String fullHttpSection = httpSection.substring(0, httpSection.indexOf("1.1") + 4).trim();
        System.out.println("start index: " + request.indexOf("HTTP/") + " end index: " + (request.indexOf("1")+3));
        //String version = request.substring(request.indexOf("HTTP/"), request.indexOf("1") + 3);
        System.out.println("version: " + fullHttpSection);
        if(!fullHttpSection.equals("HTTP/1.1")){
            outpotError = "Invalid HTTP version: " + fullHttpSection;
            return false;
        }
        System.out.println("VALIDDD!!");
        return true;
    }

    /**
     * 
     * @return The error message that was set during request validation.
     */
    public static String getOutpotError() {
        return outpotError;
    }

    public static boolean isInDirectory(String path){
        int index = path.indexOf("/");
        String workingDirectory = path.substring(index, path.indexOf("/", index + 1));
        if(workingDirectory.equals(Main.getWorkingDirectory())){
            outpotError = "File is not in the right directory";
            return false;
        }

        return true;
    }

    public static boolean isFileValid(String path, boolean isPost){
        System.out.println("B4 file path");
        System.out.println("path: " + path);
        Path file = Paths.get(path);
        System.out.println("after file path");
        System.out.println("is exists: " + Files.exists(file));
        if(!Files.isRegularFile(file) && !isPost){
            System.out.println("is post is false");
            outpotError = "File is not a regular file or doesnt exist";
            return false;
        }
        else if(isPost && Files.exists(file)){
            System.out.println("Entered exists");
            outpotError = "File already exists";
            return false;
        }
        System.out.println("INDEXING");
        int index = path.indexOf("/");
        System.out.println("index: " + index);
        String workingDirectory = path.substring(index, path.indexOf("/", index + 1));
        System.out.println("after working directory");
        //TODO: put an actual directory
        if(workingDirectory.equals("")){
            outpotError = "File is not in the right directory";
            return false;
        }

        return true;
    }

    //========= Handles methods =========

    public static byte[] getFile(String path){
        if(!isFileValid(path, false)){
            return null;
        }
        Path file = Paths.get(path);
        try {
            return Files.readAllBytes(file);
             
        } catch (Exception e) {
            // TODO: handle exception
            outpotError = "Not found";
            return null;
        }
        
    }

    public static boolean deleteFile(String path){
        //TODO: create logic
        if(!isFileValid(path, false)){
            return false;
        }
        try {
            Path file = Paths.get(path);
            Files.delete(file);
            
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            outpotError = e.getMessage();
            return false;
        }

    }

    /**Creates or Updates a file based on the data from the Post method
     * 
     * @param path
     */
    public static boolean postFile(String path, byte[] data){
        //TODO: create logic
        System.out.println("Post path: " + path);
        System.out.println("data: " + Arrays.toString(data));
        if(!isFileValid(path, true)){
            System.out.println("Entered post file validation");
            return false;
        }
        
        Path file = Paths.get(path);
        try {
            System.out.println("b4 writing data");
            System.out.println("file path: " + file.toString());
            System.out.println("data: " + Arrays.toString(data));
            Path filePath = Files.write(file, data);
            System.out.println("after writing");
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("got error on file things");
            return false;
        }
    }

    /**Replace or Creates a new resource
     * 
     * @param path
     */
    public static boolean putFile(String path, byte[] data){
        //TODO: create logic
        if(!isInDirectory(path)){
            return false;
        }
        Path file = Paths.get(path);
        try {
            Path replace = Files.write(file, data);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    /**Adds partial data
     * 
     * @param path
     */
    public static boolean patchFile(String path, byte[] data){
        //TODO: create logic
        if(!isFileValid(path, false)){
            return false;
        }
        Path file = Paths.get(path);
        
        
        try {
            if(Files.size(file) > maxSize ){
                return false;
            }
            Path append = Files.write(file, data, StandardOpenOption.APPEND);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            outpotError = e.getMessage();
            return false;
        }
        
    }

}

