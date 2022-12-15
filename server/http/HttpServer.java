package http;

import exception.ServletException;
import parser.Configuration;
import util.ServletService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final int DEFAULT_THREADS = 5;
    private static final int DEFAULT_PORT = 80;
    private static int threadsNumber = DEFAULT_THREADS;
    private static int port = DEFAULT_PORT;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
    private static ServletService service = new ServletService();

    public static void start(Configuration configuration) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(serverTask(configuration, socket));
            }
        }
    }

    private static Runnable serverTask(Configuration configuration, Socket socket) {
        return () -> {
            try {
                HttpServletRequest request = new HttpServletRequest(socket);
                if (request.getPathInfo() != null && request.getPathInfo().equals("/favicon.ico"))
                    return; // Skips /favicon.ico unnecessary requests.

                HttpServletResponse response = new HttpServletResponse(socket);
                HttpServlet servlet = service.getServlet(request, configuration);

                try {
                    servlet.service(request, response);
                } catch (ServletException | IOException e) {
                    throw new RuntimeException(e);
                }

                PrintWriter writer = response.getWriter();
                writer.flush();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
