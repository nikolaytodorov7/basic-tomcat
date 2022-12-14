package server.dispatcher;

import server.exception.ServletException;
import server.http.HttpServletRequest;
import server.http.HttpServletResponse;

public class RequestDispatcher {
    public String path;

    public RequestDispatcher(String path) {
        this.path = path;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException {

    }
}
