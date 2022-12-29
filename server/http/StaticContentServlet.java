package http;

import java.io.IOException;

public class StaticContentServlet {
    private String path;

    public StaticContentServlet(String path) {
        this.path = path;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
