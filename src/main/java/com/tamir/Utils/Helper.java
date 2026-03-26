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
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    public enum HTTP_METHODS {
        GET, POST, PUT, DELETE, PATCH
    }

    //TODO: use errors enum to check certain errors
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
                return HTTP_METHODS.GET;
            case "POST":
                return HTTP_METHODS.POST;
            case "PUT":
                return HTTP_METHODS.PUT;
            case "DELETE":
                return HTTP_METHODS.DELETE;
            case "PATCH":
                return HTTP_METHODS.PATCH;
            default:
                return null;
        }
    }




    public static Result<Void> isValidHTTPSection(String request) {
        logger.debug("validating http...");
        logger.debug("request: " + request);
        Result<Void> httpVersionRes = isValidHTTPVersion(request);
        if(!httpVersionRes.isSuccess()){
            return httpVersionRes;
        }
        Result<Void> hasMoreSpaceRes = hasSpaces(request);
        if(!hasMoreSpaceRes.isSuccess()){
            logger.debug(request);
            logger.error("Error in isValidHTTPSection" + hasMoreSpaceRes.getError());
            return hasMoreSpaceRes;
        }
        logger.debug("DONE!!! validating http");
        return Result.success(null);
    }

    /**
     * 
     * @param request HTTP request
     * @return Result of type void with success if no more than 0 space found else failure
     */
    public static Result<Void> hasSpaces(String request) {
        if(request == null){
            logger.error("Request is null in hasSpaces");
            return Result.failure("request is null");
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
        if(counter > 0){return Result.failure("Request Has space/s");}
        
        return Result.success(null);
    }

    /**Validates that the request starts with a valid HTTP method (GET, POST, PUT, DELETE, PATCH).
     * 
     * @param request The HTTP request string to validate.
     * @return true if the request starts with a valid HTTP method, false otherwise.
     */
    public static boolean startsWithHTTPMethod(String request) {
        String req = request.substring(0, request.indexOf("/")).trim();
        if(getMethod(req) != null){
            logger.debug("Request starts with HTTP method: " + req);
            return true;
        }
        logger.debug("Request does not start with HTTP method: " + req);
        return false;
    }

    /**Validates that the HTTP version in the request is correct. Currently only supports HTTP/1.1
     * 
     * @param request The HTTP request string to validate.
     * @return true if the HTTP version is valid, false otherwise.
     */
    public static Result<Void> isValidHTTPVersion(String request) {
        if(request == null){
            logger.error("Didnt find HTTP version");
            return Result.failure("Didnt find HTTP version");
        }
        
        else if(request.indexOf("HTTP/") == -1){
            logger.error("HTTP version not found");
            return Result.failure("HTTP version not found");
        }
        else if(request.indexOf("HTTP/1.1") == -1){
            logger.error("505 Unsupported Http version");
            return Result.failure("505 Unsupported Http version");
        }
        System.out.println("Extracting version");
        String httpSection = request.substring(request.indexOf("HTTP/1.1"));
        System.out.println("last index: " + (httpSection.length() - 1));
        String fullHttpSection = httpSection.substring(0, httpSection.indexOf("1.1") + 4).trim();
        System.out.println("start index: " + request.indexOf("HTTP/") + " end index: " + (request.indexOf("1")+3));
        String http = request.substring(request.indexOf("HTTP/"));
        String anotherHttpSection = http.substring(4);
        System.out.println("ANOTHER HTTP: " + anotherHttpSection);
        if(anotherHttpSection.indexOf("HTTP/") != -1){
            logger.error("Cant have more than 1 http protocol signal");
            return Result.failure("Cant have more than 1 http protocol signal");
        }
        logger.debug("version: " + fullHttpSection);
        if(!fullHttpSection.equals("HTTP/1.1")){
            return Result.failure("Invalid HTTP version: " + fullHttpSection);
        }
        logger.debug("Valid");
        return Result.success(null);
    }

    /**
     * 
     * @return The error message that was set during request validation.
     */
    public static String getOutpotError() {
        return outpotError;
    }

    public static Result<Void> isInDirectory(String path){
        Path file = Paths.get(path).normalize();
        Path workingDirectory = Paths.get(Main.getWorkingDirectory()).normalize();
        if(!file.startsWith(workingDirectory)){
            logger.debug("File isnt in the right directory");
            return Result.failure("File is not in the right directory");
        }

        return Result.success(null);
    }

    /**Validate file if hes a real file and exists in the directory unless expects to be in post where
     * it creates a new file
     * 
     * @param path path of the file. example: C:/http_test/text.txt
     * @param isPost expects to be called from post method
     * @return Result<Void> of success or failure
     */
    public static Result<Void> isFileValid(String path, boolean isPost){
        Path file = Paths.get(path).normalize();

        if(!Files.isRegularFile(file) && !isPost){
            logger.error("File is not a regular file or doesnt exist");
            return Result.failure("File is not a regular file or doesnt exist");
        }
        else if(isPost && Files.exists(file)){
            logger.error("File already exists");
            return Result.failure("File already exists");
        }
        Path workingDirectory = Paths.get(Main.getWorkingDirectory()).normalize();
        if(!file.startsWith(workingDirectory)){
            logger.error("File is not in the right directory");
            return Result.failure("File is not in the right directory");
        }

        return Result.success(null);
    }

    //========= Handles methods =========

    public static Result<byte[]> getFile(String path){
        logger.info("getting file");
        Result<Void> fileValidationRes = isFileValid(path, false);
        if(!fileValidationRes.isSuccess()){
            return Result.failure(fileValidationRes.getError());
        }
        Path file = Paths.get(path);
        try {
            byte[] data = Files.readAllBytes(file);
            return Result.success(data);
             
        } catch (Exception e) {
            logger.error("caught exception in getFile: " + e.getMessage());
            return Result.failure("Not found");
        }
        
    }

    public static Result<Void> deleteFile(String path){
        logger.info("Deleting file");
        Result<Void> fileValidationRes = isFileValid(path, false);
        if(!fileValidationRes.isSuccess()){
            return fileValidationRes;
        }
        try {
            Path file = Paths.get(path);
            Files.delete(file);
            return Result.success(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Caught exception in deleteFile: " + e.getMessage());
            return Result.failure(e.getMessage());
        }

    }

    /**Creates or Updates a file based on the data from the Post method
     * 
     * @param path
     */
    public static Result<Void> postFile(String path, byte[] data){
        logger.info("Posting file");
        logger.debug("Post path: " + path);
        logger.debug("data: " + Arrays.toString(data));
        Result<Void> fileValidationRes = isFileValid(path, true);
        if(!fileValidationRes.isSuccess()){
            return fileValidationRes;
        }
        
        Path file = Paths.get(path);
        try {
            logger.debug("b4 writing data");
            logger.debug("file path: " + file.toString());
            logger.debug("data: " + Arrays.toString(data));
            Path filePath = Files.write(file, data);
            logger.debug("after writing");
            return Result.success(null);
        } catch (Exception e) {
            logger.error("error: " + e.getMessage());
            return Result.failure(e.getMessage());
        }
    }

    /**Replace or Creates a new resource
     * 
     * @param path
     */
    public static Result<Void> putFile(String path, byte[] data){
        logger.info("Putting file");
        Result<Void> inDirResult = isInDirectory(path);
        if(!inDirResult.isSuccess()){
            return inDirResult;
        }
        Path file = Paths.get(path);
        try {
            Path replace = Files.write(file, data);
            logger.debug("replaced file");
            return Result.success(null);
        } catch (Exception e) {
            logger.error("Caught exception in putFile: " + e.getMessage());
            return Result.failure(e.getMessage());
        }
    }

    /**Adds partial data
     * 
     * @param path
     */
    public static Result<Void> patchFile(String path, byte[] data){
        logger.info("Patching file");
        Result<Void> fileValidationRes = isFileValid(path, false);
        if(!fileValidationRes.isSuccess()){
            return fileValidationRes;
        }
        Path file = Paths.get(path);
        
        
        try {
            if(Files.size(file) > maxSize ){
                return Result.failure("File is too large");
            }
            Path append = Files.write(file, data, StandardOpenOption.APPEND);
            logger.debug("appended to file");
            return Result.success(null);
        } catch (Exception e) {
            logger.error("Caught exception in patchFile: " + e.getMessage());
            return Result.failure(e.getMessage());
        }
        
    }

}

