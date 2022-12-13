package server.http;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    private Map<String, String> params = new HashMap<>();
    private HttpSession session = null;

    public String getParameter(String name) {
        return params.get(name);
    }

    public HttpSession getSession(boolean create) {
        return session;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public BufferedReader getReader(){
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String path){
        return new RequestDispatcher(path);
    }
}
