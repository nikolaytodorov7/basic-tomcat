package app.server.parser;

import app.server.http.*;

import java.util.*;

public class Configuration {
    private static Configuration configuration = null;
    public final Map<String, HttpFilter> filters = new HashMap<>();
    public final Map<String, HttpServlet> servlets = new HashMap<>();
    public final Map<String, FilterMapping> filterMappings = new HashMap<>();
    public final Map<String, ServletMapping> servletMappings = new HashMap<>();

    public static Configuration getConfiguration() {
        if (configuration == null)
            configuration = new Configuration();

        return configuration;
    }

    public void addFilterMapping(String filterName, String urlPattern, String servletName) {
        FilterMapping filterMapping = new FilterMapping(filterName, urlPattern, servletName);
//        filterMappings.put(filterName, filterMapping);
        filterMappings.put(urlPattern, filterMapping);
    }

    public void addServletMapping(String servletName, String urlPattern) {
        if (urlPattern == null || urlPattern.equals(""))
            return;

        ServletMapping servletMapping = new ServletMapping(servletName, urlPattern);
        servletMappings.put(urlPattern, servletMapping);
    }
}
