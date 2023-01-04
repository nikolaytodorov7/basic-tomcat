package app.server.http;

import java.net.Socket;

public class ServerTask {
    public static Runnable runTask(Socket socket, String contextPath, String docBase, ServletContext servletContext) {
        return () -> {
            try {
                HttpServletRequest request = new HttpServletRequest(socket.getInputStream(), contextPath, servletContext);
                if (request.getPathInfo() != null && request.getPathInfo().equals("/favicon.ico"))
                    return;

                HttpServletResponse response = new HttpServletResponse(socket, docBase, request);
                RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(request.path);
                requestDispatcher.forward(request, response);
                response.getWriter().flush();
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }
}
