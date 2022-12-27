package app.server.http;

import app.server.exception.ServletException;

import java.io.IOException;

public abstract class HttpFilter {
    private ServletContext servletContext = null;

    public HttpFilter() {
        init();
    }

    public void init() {
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }


    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
