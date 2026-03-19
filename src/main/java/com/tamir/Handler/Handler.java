package com.tamir.Handler;

import com.tamir.Models.HttpRequest;
import com.tamir.Models.HttpResponse;

public interface Handler {

    HttpResponse handleRequest(HttpRequest request);
}
