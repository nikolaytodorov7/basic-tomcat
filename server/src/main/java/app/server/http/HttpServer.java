package app.server.http;

import app.server.parser.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static app.server.util.ServletUtils.buildPath;

public class HttpServer {
    private static final int DEFAULT_THREADS = 5;
    private static final int DEFAULT_PORT = 80;
    private static int threadsNumber = DEFAULT_THREADS;
    private static int port = DEFAULT_PORT;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
    private static ServletContext servletContext;
    private static String docBase;
    private static String contextPath;

    public static void start(Configuration configuration) throws IOException {
        servletContext = new ServletContext(configuration);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(serverTask(socket));
            }
        }
    }

    private static Runnable serverTask(Socket socket) {
        return () -> {
            try {
                HttpServletRequest request = new HttpServletRequest(socket.getInputStream(), contextPath);
                if (request.getPathInfo() != null && request.getPathInfo().equals("/favicon.ico"))
                    return; // Skips /favicon.ico.

                HttpServletResponse response = new HttpServletResponse(socket);
                HttpServletResponse.docBase = docBase;

                String path = buildPath(request);

                RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
                requestDispatcher.forward(request, response);
                response.getWriter().flush();
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }
}
