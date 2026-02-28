package Src.Java;


public class Helper {
    public enum HTTP_METHODS {
        GET, POST, PUT, DELETE, PATCH
    }
    private static String outpotError = "";
    public static HTTP_METHODS currentMethod;

    public static boolean hasEcho(String request) {
        if(request == null){
            return false;
        }
        else if(request.length() < 4){
            return false;
        }
        String echoString = request.substring(0,  6);
        String echoMesage = request.substring(request.indexOf("echo") + 5, request.indexOf("HTTP"));
        if(!echoString.equals("echo/") && !echoMesage.isEmpty()){
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
    public static boolean hasPath(String request) {
        if(request == null){
            return false;
        }
        else if(request.charAt(request.indexOf("/") + 1) == ' '){
            outpotError = "Invalid path";
            return false;
        }
        else if(hasMoreThanOneSpace(request)){
            outpotError = "Invalid path: More than one space found";
            return false;
        }
        String path = request.substring(request.indexOf("/") + 1, request.indexOf("HTTP"));
        return !path.isEmpty();
    }

    public static HTTP_METHODS getMethod(String request) {
        if(request == null){
            return null;
        }
        switch (request) {
            case "GET /":
                currentMethod = HTTP_METHODS.GET;
                return HTTP_METHODS.GET;
            case "POST /":
                currentMethod = HTTP_METHODS.POST;
                return HTTP_METHODS.POST;
            case "PUT /":
                currentMethod = HTTP_METHODS.PUT;
                return HTTP_METHODS.PUT;
            case "DELETE /":
                currentMethod = HTTP_METHODS.DELETE;
                return HTTP_METHODS.DELETE;
            case "PATCH /":
                currentMethod = HTTP_METHODS.PATCH;
                return HTTP_METHODS.PATCH;
            default:
                return null;
        }
    }

    public static HTTP_METHODS getCurrentMethod() {
        return currentMethod;
    }

    // public static void handleRequest(String request, OutputStream outputStream) {
    //     if(request == null){
    //         return;
    //     }
    //     else if(!startsWithHTTPMethod(request)){
    //         try{
    //             outputStream.write("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nBad Request".getBytes());
    //             outputStream.flush();
    //         }
    //         catch(IOException e){
    //             e.printStackTrace();
    //         }
    //         return;
    //     }
        
    //     switch (currentMethod) {
    //         case GET:
    //             System.out.println("GET request received");
    //             boolean success = handleGetMethod(request, outputStream);
    //             break;
    //         case POST:
    //             System.out.println("POST request received");
    //             break;
    //         case PUT:
    //             System.out.println("PUT request received");
    //             break;
    //         case DELETE:
    //             System.out.println("DELETE request received");
    //             break;
    //         case PATCH:
    //             System.out.println("PATCH request received");
    //             break;
    //         default:
    //             break;
    //     }
    // }

    public static boolean isValidGetRequest(String request) {
        try{
            String currentDir = System.getProperty(Main.getWorkingDirectory());
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // public static boolean handleGetMethod(String request, OutputStream outputStream){
    //     System.out.println("outputStream null? " + (outputStream == null));
    //     try{
            
    //         if(request == null){
    //             outputStream.write("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nBad Request".getBytes());
    //             outputStream.flush();
    //             return false;
    //         }
    //         else if(hasEcho(request)){
    //             String echoMessage = request.substring(request.indexOf("echo") + 4).trim();
    //             String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\n" + echoMessage;
    //             outputStream.write(response.getBytes());
    //             outputStream.flush();
    //             return true;
    //         }
    //         String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, World!";
    //         outputStream.write(response.getBytes());
    //         outputStream.flush();
    //         return true;
    //     }
    //     catch(IOException e){
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

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
        if(request == null){
            return false;
        }
        else if(isValidHTTPVersion(request)){
            outpotError = "HTTP version not found";
            return false;
        }
        else if(!hasMoreThanOneSpace(request)){
            outpotError = "More than one space found";
            return false;
        }
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
        if(request == null){
            System.out.println("Request is null");
            return false;
        }
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
            return false;
        }
        else if(request.indexOf("HTTP/1.1") == -1){
            outpotError = "HTTP version not found";
            return false;
        }
        String version = request.substring(request.indexOf("HTTP/"), request.indexOf("\r\n"));
        if(!version.equals("HTTP/1.1")){
            outpotError = "Invalid HTTP version: " + version;
            return false;
        }
        return true;
    }

    /**
     * 
     * @return The error message that was set during request validation.
     */
    public static String getOutpotError() {
        return outpotError;
    }

}

