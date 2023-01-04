package app.server.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class HttpServletRequest {
    private static final Set<String> VALID_HTTP_METHODS = new HashSet<>(Set.of("GET", "POST", "PUT", "DELETE"));
    private static final Set<String> VALID_HTTP_PROTOCOLS = new HashSet<>(Set.of(
            "HTTP/0.9", "HTTP/1.0", "HTTP/1.1", "HTTP/2", "HTTP/3"));

    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    ServletContext servletContext;
    private HttpSession session = null;
    private String servletPath = null;
    private String queryString = null;
    private String pathInfo = null;
    private BufferedReader reader;
    private String protocol;
    private String method;
    private Cookie[] cookies;
    String path;

    public HttpServletRequest(InputStream inputStream, String contextPath, ServletContext servletContext) throws Exception {
        this.servletContext = servletContext;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        String[] methodPathBody = line.split(" ");
        if (methodPathBody.length != 3)
            throw new Exception("First line must contain method, protocol and path");

        method = methodPathBody[0];
        if (!VALID_HTTP_METHODS.contains(method))
            throw new Exception("Invalid method!");

        path = methodPathBody[1];
        if (path.startsWith(contextPath))
            path = path.substring(contextPath.length());

        splitPath(path);
        protocol = methodPathBody[2];
        if (!VALID_HTTP_PROTOCOLS.contains(protocol))
            throw new Exception(String.format("Invalid protocol: %s", protocol));

        extractHeaders();
        processCookies();
    }

    private void processCookies() { //todo check if we must send cookie each response or only first
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null) {
            cookies = new Cookie[0];
            return;
        }

        List<Cookie> cookiesList = new ArrayList<>();
        String[] splitCookies = cookieHeader.split(";");
        for (String stringCookie : splitCookies) {
            stringCookie = stringCookie.trim();
            String[] splitCookie = stringCookie.split("=");
            String cookieName = splitCookie[0];
            String cookieValue = splitCookie[1];
            if (cookieName.equals("Session")) {
                session = servletContext.sessions.get(cookieValue);
                if (session == null)
                    continue;
            }

            Cookie cookie = new Cookie(cookieName, cookieValue);
            cookiesList.add(cookie);
        }

        cookies = cookiesList.toArray(new Cookie[0]);
    }

    void splitPath(String path) {
        String[] split = path.split("/");
        if (split.length < 2)
            return;

        String[] splitQuery = split[1].split("\\?");
        servletPath = '/' + splitQuery[0];
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

    public Cookie[] getCookies() {
        return cookies;
    }

    public HttpSession getSession() {
        if (session == null)
            session = new HttpSession(servletContext);

        return session;
    }

    public HttpSession getSession(boolean create) {
        if (create)
            session = new HttpSession(servletContext);

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
        while ((line = reader.readLine()) != null && !line.isBlank()) {
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
