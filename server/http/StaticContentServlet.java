package http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static http.HttpResponse.getResponse;

public class StaticContentServlet {
    private String path;

    public StaticContentServlet(String path) throws IOException {
        this.path = path;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        if (!request.getMethod().equals("GET")) {
            String msg = String.format("HTTP Status 404 – Not Found\nThe requested resource [%s] is not available", path);
            writer.println(msg);
            return;
        }

        HttpResponse httpResponse = getResponse(writer, request.getProtocol(), path);
        if (httpResponse == null) {
            String msg = String.format("HTTP Status 404 – Not Found\nThe requested resource [%s] is not available", path);
            writer.println(msg);
            return;
        }

        OutputStream outputStream = response.getOutputStream();
        httpResponse.send(writer, outputStream);
    }
}
