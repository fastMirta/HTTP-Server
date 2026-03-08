package Src.Java;

import Src.Java.Handler.EchoHandler;
import Src.Java.Handler.FileHandler;
import Src.Java.Handler.Handler;
import Src.Java.Handler.RootHandler;
import Src.Java.Models.HttpRequest;
import Src.Java.Utils.Helper;

public class Router {
    public static Handler routing(HttpRequest request){
        if(Helper.hasEcho(request.getPath())){
            System.out.println("routing echo");
            return new EchoHandler();
        }
        else if(Helper.hasPath(request.getPath())){
            System.out.println("router path: " + request.getPath());
            System.out.println("routing file");
            return new FileHandler();
        }
        System.out.println("routing root");
        return new RootHandler();
    }
}
