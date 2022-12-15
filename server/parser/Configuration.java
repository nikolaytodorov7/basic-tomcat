package parser;

import http.HttpFilter;
import http.HttpServlet;

import java.util.*;

public class Configuration {
    public final Map<String, HttpFilter> filters = new HashMap<>();
    public final Map<String, HttpServlet> servlets = new HashMap<>();
    public final Map<String, FilterMapping> filterMappings = new HashMap<>();
    public final Map<String, ServletMapping> servletMappings = new HashMap<>();

    public void addFilterMapping(String filterName, String urlPattern, String servletName) {
        if ((urlPattern == null || urlPattern.equals("")) && ((servletName == null || servletName.equals(""))))
            return;

        FilterMapping filterMapping = new FilterMapping(filterName, urlPattern, servletName);
        filterMappings.put(filterName, filterMapping);
    }

    public void addServletMapping(String servletName, String urlPattern) {
        if (urlPattern == null || urlPattern.equals(""))
            return;

        ServletMapping servletMapping = new ServletMapping(servletName, urlPattern);
        servletMappings.put(urlPattern, servletMapping);
    }
}
