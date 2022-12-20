package http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    private Map<String, String> headers = new HashMap<>();
    private PrintWriter printWriter;
    private OutputStream outputStream;
    int status = 200;

    public HttpServletResponse(Socket socket) {
        try {
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public int getStatus() {
        return status;
    }
}
