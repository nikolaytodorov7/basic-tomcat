package server.http;

import server.exception.ServletException;

import java.io.IOException;

public class RequestDispatcher {
    public String path;

    public RequestDispatcher(String path) {
        this.path = path;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException {

    }
}
