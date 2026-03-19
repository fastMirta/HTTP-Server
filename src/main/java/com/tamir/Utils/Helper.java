package com.tamir.Utils;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tamir.Main;

public class Helper {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public enum HTTP_METHODS {
        GET, POST, PUT, DELETE, PATCH
    }
    public enum ERRORS {
        
        ECHO_INVALID_PARAM,
        ECHO_MUST_CONTAIN_LETTERS,
        ECHO_TOO_SHORT,
        REQUEST_NULL,
        INVALID_PATH,

        // File errors
        FILE_NOT_FOUND,
        FILE_ALREADY_EXISTS,
        FILE_TOO_LARGE,
        
        // HTTP errors
        INVALID_HTTP_VERSION,
        UNSUPPORTED_HTTP_VERSION,
        METHOD_NOT_ALLOWED
    }
    private static String outpotError = "";
    private static final long maxSize = 10L * 1024 * 1024 * 1024;

    public static HTTP_METHODS currentMethod;

    public static Result<Void> hasEcho(String request) {
        if(request == null){
            return Result.failure("Request null");
        }
        else if(request.length() < 7){
            System.out.println("Request too short: " + request);
            return Result.failure("Request Too short");
        }
        System.out.println("REQUESRT BESTY: " + request);
        String echoString = request.substring(0,  6);
        String echoMesage = request.substring(6);
        String regEx = "abcdefghijklmnopqrstuvwxyz";
        System.out.println("ECHO PATH: " + echoString);
        if(!echoString.equals("/echo/")){
            return Result.failure("Request dont have echo");
        }
        else if(echoMesage.isEmpty() || echoMesage.contains(" ")){
            return Result.failure("Invalid echo parameter");
        }
        char[] characters = echoMesage.toCharArray();
        for(char c : characters){
            System.out.println(regEx.indexOf(echoString));
            if(regEx.indexOf(c) != -1 || regEx.toUpperCase().indexOf(c) != -1){
                return Result.success(null);
            }
        }
        return Result.failure("Echo must contain letters");
    }

    /**Validates that the request contains a path after the HTTP method and before the HTTP version. For example, in "GET /path HTTP/1.1", the path is "/path". The method checks that there is a non-empty path and that it does not contain invalid characters or multiple spaces.
     * 
     * @param request The HTTP request string to validate. This should be the part of the request line after the HTTP method, for example: "/path HTTP/1.1".
     * @return true if the request contains a valid path, false otherwise. If the path is invalid, an appropriate error message is set in the outpotError variable.
     */
    public static Result<Void> hasPath(String path) {
        if(path == null){
            return Result.failure("Path is null");
        }
        else if(path.trim().equals("/")){
            return Result.failure("Dont have path"); //shouldnt be an error cuz it just will be root
        }
        //boolean containsLetters = path.toLowerCase().contains("abcdefghijklmnopqrstuvwxyz");
        System.out.println("PATH: " + path);
        //System.out.println("contains letter: " + containsLetters);
        System.out.println("isnt empty: " + !path.isEmpty());
        System.out.println("doesnt contains space?: " + !path.contains(" "));
        if((path.isEmpty() || path.contains(" "))){
            return Result.failure("Invalid path");
        }
        return Result.success(null);
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
        logger.debug("validating http...");
        logger.debug("request: " + request);
        if(!isValidHTTPVersion(request)){
            System.out.println("Not Valid");
            //outpotError = "HTTP version not found";
            return false;
        }
        else if(hasMoreThanOneSpace(request)){
            System.out.println(request);
            System.out.println("No space valid");
            outpotError = "More than one space found";
            logger.error("More than one space found");
            return false;
        }
        System.out.println("DONE!!! validating http");
        return true;
    }

    public static boolean hasMoreThanOneSpace(String request) {
        if(request == null){
            return false;
        }
        
        int counter = 0;
        for(int i = 0; i < request.length(); i++){
            if(request.charAt(i) == ' '){
                counter++;
            }
            else{
                break;
            }
        }
        if(counter == 0){return false;}
        
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
        
        else if(request.indexOf("HTTP/") == -1){
            outpotError = "HTTP version not found";
            logger.error("HTTP version not found");
            return false;
        }
        else if(request.indexOf("HTTP/1.1") == -1){
            outpotError = "505 Unsupported Http version";
            return false;
        }
        System.out.println("Extracting version");
        String httpSection = request.substring(request.indexOf("HTTP/1.1"));
        System.out.println("last index: " + (httpSection.length() - 1));
        String fullHttpSection = httpSection.substring(0, httpSection.indexOf("1.1") + 4).trim();
        System.out.println("start index: " + request.indexOf("HTTP/") + " end index: " + (request.indexOf("1")+3));
        //String version = request.substring(request.indexOf("HTTP/"), request.indexOf("1") + 3);
        String http = request.substring(request.indexOf("HTTP/"));
        String anotherHttpSection = http.substring(4);
        System.out.println("ANOTHER HTTP: " + anotherHttpSection);
        if(anotherHttpSection.indexOf("HTTP/") != -1){
            logger.error("Cant have more than 1 http protocol signal");
            outpotError = "Cant have more than 1 http protocol signal";
            return false;
        }
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
        if(!workingDirectory.equals(Main.getWorkingDirectory())){
            outpotError = "File is not in the right directory";
            return false;
        }

        return true;
    }

    public static boolean isFileValid(String path, boolean isPost){
        Path file = Paths.get(path);

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

    public static Result<byte[]> getFile(String path){
        if(!isFileValid(path, false)){
            return Result.failure(outpotError);
        }
        Path file = Paths.get(path);
        try {
            byte[] data = Files.readAllBytes(file);
            return Result.success(data);
             
        } catch (Exception e) {
            return Result.failure("Not found");
            
        }
        
    }

    public static Result<Void> deleteFile(String path){
        //TODO: create logic
        if(!isFileValid(path, false)){
            return Result.failure(outpotError);
        }
        try {
            Path file = Paths.get(path);
            Files.delete(file);
            return Result.success(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.failure(e.getMessage());
        }

    }

    /**Creates or Updates a file based on the data from the Post method
     * 
     * @param path
     */
    public static Result<Void> postFile(String path, byte[] data){
        //TODO: create logic
        System.out.println("Post path: " + path);
        System.out.println("data: " + Arrays.toString(data));
        if(!isFileValid(path, true)){
            return Result.failure(outpotError);
        }
        
        Path file = Paths.get(path);
        try {
            System.out.println("b4 writing data");
            System.out.println("file path: " + file.toString());
            System.out.println("data: " + Arrays.toString(data));
            Path filePath = Files.write(file, data);
            System.out.println("after writing");
            return Result.success(null);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("got error on file things");
            return Result.failure(e.getMessage());
        }
    }

    /**Replace or Creates a new resource
     * 
     * @param path
     */
    public static Result<Void> putFile(String path, byte[] data){
        //TODO: create logic
        if(!isInDirectory(path)){
            return Result.failure(outpotError);
        }
        Path file = Paths.get(path);
        try {
            Path replace = Files.write(file, data);
            return Result.success(null);
        } catch (Exception e) {
            // TODO: handle exception
            return Result.failure(e.getMessage());
        }
    }

    /**Adds partial data
     * 
     * @param path
     */
    public static Result<Void> patchFile(String path, byte[] data){
        //TODO: create logic
        
        if(!isFileValid(path, false)){
            return Result.failure(outpotError);
        }
        Path file = Paths.get(path);
        
        
        try {
            if(Files.size(file) > maxSize ){
                return Result.failure("File is too large");
            }
            Path append = Files.write(file, data, StandardOpenOption.APPEND);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
        
    }

}

