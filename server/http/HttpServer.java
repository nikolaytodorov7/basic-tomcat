package http;

import exception.ServletException;
import parser.Configuration;
import util.ServletContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.ServletUtils.buildPath;

public class HttpServer {
    private static final int DEFAULT_THREADS = 5;
    private static final int DEFAULT_PORT = 80;
    private static int threadsNumber = DEFAULT_THREADS;
    private static int port = DEFAULT_PORT;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
    private static ServletContext servletService;

    public static void start(Configuration configuration) throws IOException {
        servletService = new ServletContext(configuration);
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
                HttpServletRequest request = new HttpServletRequest(socket);
                if (request.getPathInfo() != null && request.getPathInfo().equals("/favicon.ico"))
                    return; // Skips /favicon.ico.

                HttpServletResponse response = new HttpServletResponse(socket);

                String path = buildPath(request);
                HttpServlet servlet = servletService.getServlet(path);
                if (servlet == null) {
                    StaticContentServlet staticContentServlet = new StaticContentServlet(path);
                    staticContentServlet.service(request, response);
                    PrintWriter writer = response.getWriter();
                    writer.flush();
                    socket.close();
                    return;
                }

                try {
                    servlet.service(request, response);
                } catch (ServletException | IOException e) {
                    throw new RuntimeException(e);
                }

                PrintWriter writer = response.getWriter();
                writer.flush();
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }
}
