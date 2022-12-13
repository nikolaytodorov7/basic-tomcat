package server.http;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    public static final int SC_OK = 200;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_NOT_FOUND = 400;
    private String contentType = null;
    private Map<String, String> headers = new HashMap<>();
    private PrintWriter printWriter;

    public PrintWriter getWriter() {
        return printWriter;
    }

    public void setContentType(String type) {
        contentType = type;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addHeader(String name, String value) {

    }
}
