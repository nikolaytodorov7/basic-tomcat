package server.http;

import server.dispatcher.RequestDispatcher;
import server.util.ConsoleMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpServletRequest {
    private Set<String> VALID_METHODS = new HashSet<>(Set.of("GET", "POST", "PUT", "DELETE"));
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private HttpSession session = null;
    private BufferedReader reader;
    private String servletPath;
    private String queryString;
    private String body = null;
    private String pathInfo;
    private String protocol;
    private String method;
    private String path;

    public HttpServletRequest(InputStream inputStream) throws IOException {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        String[] methodPathBody = line.split(" ");
        if (methodPathBody.length != 3)
            throw new IOException("First line must contain method, protocol and path");

        method = methodPathBody[0];
        if (!VALID_METHODS.contains(method))
            throw new IOException("Invalid method!");

        path = methodPathBody[1]; // todo split servletPath, queryString, pathInfo
        protocol = methodPathBody[2]; // todo validate

        extractHeaders();

        //todo body extract
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

    public HttpSession getSession(boolean create) {
        if (create)
            session = new HttpSession();

        return session;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public BufferedReader getReader() {
        return reader;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return new RequestDispatcher(path);
    }

    private void extractHeaders() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank())
                break;

            String[] entry = line.split(": ");
            if (entry.length != 2) {
                ConsoleMessage.printBadRequest();
                return;
            }

            String key = entry[0];
            String value = entry[1];
            headers.put(key, value);
        }
    }
}
