package http;

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
    private static ServletContext servletContext;
    private static String docBase;
    private static String contextPath;

    public static void start(Configuration configuration, String contextPath, String docBase) throws IOException {
        HttpServer.docBase = docBase;
        HttpServer.contextPath = contextPath;
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
                HttpServletRequest request = new HttpServletRequest(socket, contextPath);
                if (request.getPathInfo() != null && request.getPathInfo().equals("/favicon.ico"))
                    return; // Skips /favicon.ico.

                HttpServletResponse response = new HttpServletResponse(socket);

                String path = buildPath(request);
                HttpServlet servlet = servletContext.getServlet(path);
                if (servlet == null) {
                    HttpServletResponse.docBase = docBase;
                    StaticContentServlet staticContentServlet = new StaticContentServlet(path);
                    staticContentServlet.service(request, response);
                    PrintWriter writer = response.getWriter();
                    if (response.staticResponseFailed) {
                        response.setHeader("Content-type", "text/plain");
                        String msg = String.format("HTTP Status 404 – Not Found\nThe requested resource [%s] is not available", path);
                        writer.println(msg);
                    }

                    writer.flush();
                    socket.close();
                    return;
                }

                response.setHeader("Content-type", "text/plain");
                servlet.service(request, response);
                PrintWriter writer = response.getWriter();
                writer.flush();
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }
}
