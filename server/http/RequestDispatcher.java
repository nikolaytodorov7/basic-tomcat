package http;

import exception.ServletException;
import parser.Configuration;
import util.ServletContext;

import java.io.IOException;
import java.io.PrintWriter;

public class RequestDispatcher {
    private String path;
    private HttpServlet servlet;

    public RequestDispatcher(String path) {
        this.path = path;
        Configuration configuration = Configuration.getConfiguration();
        ServletContext servletContext = new ServletContext(configuration);
        servlet = servletContext.getServlet(path);
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (servlet == null) {
            StaticContentServlet staticContentServlet = new StaticContentServlet(path);
            try {
                staticContentServlet.service(request, response);
                PrintWriter writer = response.getWriter();
                writer.flush();
            } catch (IOException e) {
                response.sendError(path);
                throw new RuntimeException(e);
            }

            if (response.staticResponseFailed)
                response.sendError(path);

            return;
        }

        request.path = path;
        request.splitPath(path);

        try {
            servlet.service(request, response);
            response.setHeader("Content-type", "text/plain");
        } catch (IOException e) {
            response.sendError(path);
        }
    }
}
