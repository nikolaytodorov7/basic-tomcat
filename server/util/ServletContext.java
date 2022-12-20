package util;

import http.HttpServlet;
import parser.Configuration;
import parser.ServletMapping;

import java.util.Map;
import java.util.regex.Pattern;

public class ServletContext {
    private Configuration configuration;

    public ServletContext(Configuration configuration) {
        this.configuration = configuration;
    }

    public HttpServlet getServlet(String path) {
        ServletMapping servletMapping = getServletMapping(path);
        if (servletMapping == null) {
            return null;
        }

        String servletName = servletMapping.servletName;
        if (servletName == null)
            return null;

        return configuration.servlets.get(servletName);
    }

    private ServletMapping getServletMapping(String path) {
        for (Map.Entry<String, ServletMapping> entry : configuration.servletMappings.entrySet()) {
            String servletPath = entry.getKey();
            int index = servletPath.indexOf("*");
            if (index != -1) {
                String beforeStar = servletPath.substring(0, index);
                Pattern starPattern;
                if (index + 1 < servletPath.length()) {
                    String afterStar = servletPath.substring(index + 1);
                    starPattern = Pattern.compile(beforeStar + "(.*?)" + afterStar);
                } else
                    starPattern = Pattern.compile(beforeStar + "(.*?)");

                if (starPattern.matcher(path).matches()) {
                    return entry.getValue();
                }
            }

            Pattern pattern = Pattern.compile(servletPath);
            if (pattern.matcher(path).matches())
                return entry.getValue();
        }

        return null;
    }
}
