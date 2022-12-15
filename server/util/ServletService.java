package util;

import http.HttpServlet;
import http.HttpServletRequest;
import parser.Configuration;
import parser.ServletMapping;

public class ServletService {
    public HttpServlet getServlet(HttpServletRequest request, Configuration configuration) {
        String servletPath = request.getServletPath();
        ServletMapping servletMapping = configuration.servletMappings.get(servletPath + "/*"); // todo expand program to work with *
        if (servletMapping == null) {
            System.out.println("servletMapping == null"); // todo HTTP Status 404 – Not Found, The requested resource [/tomcat-rest-api/post] is not available
            return null;
        }

        String servletName = servletMapping.servletName;
        if (servletName == null)
            return null; // todo some error and notify user

        return configuration.servlets.get(servletName);
    }
}
