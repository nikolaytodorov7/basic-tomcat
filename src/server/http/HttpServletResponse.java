package server.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    private String contentType = null;
    private Map<String, String> headers = new HashMap<>();
    private PrintWriter printWriter;

    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    public void setContentType(String type) {
        contentType = type;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public int getStatus() {
        return 0;
    }
}
