package util;

import http.HttpServletRequest;

public class ServletUtils {
    public static String buildPath(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String queryString = req.getQueryString();
        StringBuilder path = new StringBuilder();
        if (servletPath != null)
            path.append(servletPath);

        if (pathInfo != null)
            path.append(pathInfo);

        if (queryString != null)
            path.append("?").append(queryString);

        return path.toString();
    }
}
