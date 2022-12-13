package server.http;

import server.exception.ServletException;

import java.io.IOException;

public class HttpServlet {
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String protocol = req.getProtocol();
//        String msg = lStrings.getString("server.http.method_get_not_supported");
//        resp.sendError(getMethodNotSupportedCode(protocol), msg);
    }
}
