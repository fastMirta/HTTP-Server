package com.tamir;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.tamir.Utils.Result;
import com.tamir.Models.HttpRequest;
import java.io.IOException;

public class ParserTest {

    @Test
    void shouldReturnFailureWhenRequestIsNull() throws IOException {
        Result<HttpRequest> result = Parser.parseRequest(null);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldReturnFailureWhenRequestIsEmpty() throws IOException {
        Result<HttpRequest> result = Parser.parseRequest("");
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldParseValidGetRequest() throws IOException {
        Result<HttpRequest> result = Parser.parseRequest("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(result.isSuccess());
        assertEquals("GET", result.getData().getMethod().toString());
        assertEquals("/", result.getData().getPath());
    }

    @Test
    void shouldReturnFailureForInvalidHTTPVersion() throws IOException {
        Result<HttpRequest> result = Parser.parseRequest("GET / HTTP/2.0\r\nHost: localhost\r\n\r\n");
        assertFalse(result.isSuccess());
    }
}


