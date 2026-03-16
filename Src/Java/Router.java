package Src.Java;

import Src.Java.Handler.EchoHandler;
import Src.Java.Handler.FileHandler;
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
            || echoResult.getError().equals("Request Too short")
        ){
            return null;
        }
        else if(Helper.hasPath(request.getPath())){
            System.out.println("error: " + echoResult.getError());
            System.out.println(echoResult.getError().equals("Invalid echo parameter"));
            System.out.println(Helper.getOutpotError().equals("Invalid echo parameter"));
            System.out.println("router path: " + request.getPath());
            System.out.println("routing file");
            return new FileHandler();
        }
        System.out.println("routing root");
        return new RootHandler();
    }
}
