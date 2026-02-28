package Src.Java.Handler;

import Src.Java.Models.HttpRequest;
import Src.Java.Models.HttpResponse;

public interface Handler {

    HttpResponse handleRequest(HttpRequest request);
}
