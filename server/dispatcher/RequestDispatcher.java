package dispatcher;

import exception.ServletException;
import http.HttpServletRequest;
import http.HttpServletResponse;

public class RequestDispatcher {
    public String path;

    public RequestDispatcher(String path) {
        this.path = path;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException {

    }
}