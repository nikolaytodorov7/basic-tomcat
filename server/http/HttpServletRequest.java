package http;

import dispatcher.RequestDispatcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpServletRequest {
    private static final Set<String> VALID_HTTP_METHODS = new HashSet<>(Set.of("GET", "POST", "PUT", "DELETE"));
    private static final Set<String> VALID_HTTP_PROTOCOLS = new HashSet<>(Set.of(
            "HTTP/0.9", "HTTP/1.0", "HTTP/1.1", "HTTP/2", "HTTP/3"));
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private HttpSession session = null;
    private BufferedReader reader;
    private String servletPath = null;
    private String queryString = null;
    private String pathInfo = null;
    private String protocol;
    private String method;
    private String path;

    public HttpServletRequest(Socket socket) throws Exception {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        String[] methodPathBody = line.split(" ");
        if (methodPathBody.length != 3)
            throw new Exception("First line must contain method, protocol and path");

        method = methodPathBody[0];
        if (!VALID_HTTP_METHODS.contains(method))
            throw new Exception("Invalid method!");

        path = methodPathBody[1];
        splitPath(path);
        protocol = methodPathBody[2];
        if (!VALID_HTTP_PROTOCOLS.contains(protocol))
            throw new Exception(String.format("Invalid protocol: %s", protocol));

        extractHeaders();
    }

    private void splitPath(String path) {
        String[] split = path.split("/");
        if (split.length < 2)
            return;

        servletPath = '/' + split[1];
        StringBuilder buildPathInfo = new StringBuilder();
        for (int i = 2; i < split.length; i++) {
            buildPathInfo.append('/').append(split[i]);
        }

        pathInfo = buildPathInfo.toString();

        split = path.split("\\?");
        if (split.length > 1)
            queryString = split[split.length - 1];
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpSession getSession(boolean create) {
        if (create)
            session = new HttpSession();

        return session;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return new RequestDispatcher(path);
    }

    private void extractHeaders() throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank())
                break;

            String[] entry = line.split(": ");
            if (entry.length != 2) {
                throw new Exception(String.format("Invalid headers '%s'", line));
            }

            String key = entry[0];
            String value = entry[1];
            headers.put(key, value);
        }
    }
}
