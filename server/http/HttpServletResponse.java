package http;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    private Map<String, String> headers = new HashMap<>();
    private PrintWriter printWriter;

    public HttpServletResponse(Socket socket) {
        try {
            printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public int getStatus() {
        return 0;
    }
}
