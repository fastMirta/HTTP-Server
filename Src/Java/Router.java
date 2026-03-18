package Src.Java;

import Src.Java.Handler.EchoHandler;
import Src.Java.Handler.FileHandler;
import Src.Java.Handler.ErrorHandler;
import Src.Java.Handler.Handler;
import Src.Java.Handler.RootHandler;
import Src.Java.Models.HttpRequest;
import Src.Java.Utils.Helper;
import Src.Java.Utils.Result;

public class Router {
    public static Handler routing(HttpRequest request){
        Result<Void> echoResult = Helper.hasEcho(request.getPath());
        if(echoResult.isSuccess()){
            System.out.println("routing echo");
            return new EchoHandler();
        }
        else if(echoResult.getError().equals("Invalid echo parameter") || echoResult.getError().equals("Request null")
            // || echoResult.getError().equals("Request Too short")
        ){
            return new ErrorHandler(echoResult.getError());
        }
        else if(echoResult.getError().equals("Echo must contain letters")){
            return new ErrorHandler(echoResult.getError());
        }
        Result<Void> pathResult = Helper.hasPath(request.getPath());
        System.out.println(pathResult.isSuccess());
        if(pathResult.isSuccess()){
            System.out.println("error: " + pathResult.getError());
            System.out.println(pathResult.getError().equals("Invalid path"));
            System.out.println("router path: " + request.getPath());
            System.out.println("routing file");
            return new FileHandler();
        }
        else if(pathResult.getError().equals("Invalid path")){
            return new ErrorHandler(pathResult.getError());
        }
        System.out.println("routing root");
        return new RootHandler();
    }
}
