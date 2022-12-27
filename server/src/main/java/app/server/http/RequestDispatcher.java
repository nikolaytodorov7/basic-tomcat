package app.server.http;

import app.server.exception.ServletException;
import app.server.parser.Configuration;
import app.server.parser.FilterMapping;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestDispatcher {
    private String path;
    private HttpServlet servlet;
    private FilterChain filterChain = getFilterChain();

    public RequestDispatcher(String path) {
        this.path = path;
        Configuration configuration = Configuration.getConfiguration();
        ServletContext servletContext = new ServletContext(configuration);
        servlet = servletContext.getServlet(path);
        filterChain = getFilterChain();
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FilterChain filterChain = getFilterChain();
        filterChain.doFilter(request, response);
        if (servlet == null) {
            doStaticContextServlet(request, response);
            return;
        }

        normalServlet(request, response);
    }

    private void normalServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        request.path = path;
        request.splitPath(path);

        try {
            servlet.service(request, response);
            response.setHeader("Content-type", "text/plain");
        } catch (IOException e) {
            response.sendError(path);
        }
    }

    private void doStaticContextServlet(HttpServletRequest request, HttpServletResponse response) {
        StaticContentServlet staticContentServlet = new StaticContentServlet(path);
        try {
            staticContentServlet.doGet(request, response);
        } catch (IOException e) {
            response.sendError(path);
            throw new RuntimeException(e);
        }

        if (response.staticResponseFailed)
            response.sendError(path);
    }

    private FilterChain getFilterChain() {
        Configuration configuration = Configuration.getConfiguration();
        FilterChain chain = new FilterChain();
        for (Map.Entry<String, FilterMapping> entry : configuration.filterMappings.entrySet()) {
            FilterMapping filterMapping = entry.getValue();
            String urlPattern = filterMapping.urlPattern;
            int index = urlPattern.indexOf("*");
            if (index != -1) {
                String beforeStar = urlPattern.substring(0, index);
                Pattern starPattern;
                if (index + 1 < urlPattern.length()) {
                    String afterStar = urlPattern.substring(index + 1);
                    starPattern = Pattern.compile(beforeStar + "(.*?)" + afterStar);
                } else
                    starPattern = Pattern.compile(beforeStar + "(.*?)");


                if (starPattern.matcher(path).matches()) {
                    HttpFilter httpFilter = configuration.filters.get(entry.getValue().filterName);
                    chain.addFilter(httpFilter);
                    continue;
                }
            }

            Pattern pattern = Pattern.compile(urlPattern);
            if (pattern.matcher(path).matches()) {
                HttpFilter httpFilter = configuration.filters.get(entry.getValue().filterName);
                chain.addFilter(httpFilter);
            }
        }

        return chain;
    }
}
