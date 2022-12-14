package server.parser;

import server.http.HttpFilter;
import server.http.HttpServlet;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public final Map<String, HttpFilter> filters = new HashMap<>();
    public final Map<String, HttpServlet> servlets = new HashMap<>();
    public final Map<String, FilterMapping> filterMappings = new HashMap<>();
    public final Map<String, ServletMapping> servletMappings = new HashMap<>();

    public void addFilterMapping(String filterName, String urlPattern, String servletName) {
        if ((urlPattern == null || urlPattern.equals("")) && ((servletName == null || servletName.equals(""))))
            return;

        if (!filterMappings.containsKey(filterName)) {
            FilterMapping filterMapping = new FilterMapping(filterName);
            if (urlPattern != null && !urlPattern.equals(""))
                filterMapping.urlPatterns.add(urlPattern);

            if (servletName != null && !servletName.equals(""))
                filterMapping.servletNames.add(servletName);

            filterMappings.put(filterName, filterMapping);
            return;
        }

        FilterMapping filterMapping = filterMappings.get(filterName);
        if (urlPattern != null)
            filterMapping.urlPatterns.add(urlPattern);

        if (servletName != null)
            filterMapping.servletNames.add(servletName);
    }

    public void addServletMapping(String servletName, String urlPattern) {
        if (urlPattern == null || urlPattern.equals(""))
            return;

        if (!servletMappings.containsKey(servletName)) {
            ServletMapping servletMapping = new ServletMapping(servletName);
            servletMapping.urlPatterns.add(urlPattern);
            servletMappings.put(servletName, servletMapping);
            return;
        }

        ServletMapping servletMapping = servletMappings.get(servletName);
        servletMapping.urlPatterns.add(urlPattern);
    }
}
