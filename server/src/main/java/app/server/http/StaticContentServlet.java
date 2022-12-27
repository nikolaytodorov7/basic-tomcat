package app.server.http;

import java.io.IOException;

public class StaticContentServlet extends HttpServlet {
    private String path;

    public StaticContentServlet(String path) {
        this.path = path;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!request.getMethod().equals("GET")) {
            response.staticResponseFailed = true;
            return;
        }

        response.prepareStaticResponse(request.getProtocol(), path);
        if (response.staticResponseFailed)
            return;

        response.send();
    }
}
