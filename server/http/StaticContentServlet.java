package http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class StaticContentServlet {
    private String path;

    public StaticContentServlet(String path) {
        this.path = path;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        if (!request.getMethod().equals("GET")) {
            response.staticResponseFailed = true;
            return;
        }

        response.prepareStaticResponse(writer, request.getProtocol(), path);
        if (response.staticResponseFailed)
            return;

        response.send();
    }
}
