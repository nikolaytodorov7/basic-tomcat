package app.server.http;

import app.server.exception.ServletException;

import java.io.IOException;
import java.util.ArrayDeque;

public class FilterChain {
    private final ArrayDeque<HttpFilter> filterQueue = new ArrayDeque<>();

    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        while (!filterQueue.isEmpty()) {
            HttpFilter filter = filterQueue.pop();
            filter.doFilter(request, response, this);
        }
    }

    void addFilter(HttpFilter filter) {
        filterQueue.offer(filter);
    }
}
