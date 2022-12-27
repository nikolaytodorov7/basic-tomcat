package app.webapp.filter;

import app.webapp.util.ServletUtility;
import app.server.exception.ServletException;
import app.server.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoggerFilter extends HttpFilter {
    private static final Logger LOGGER = LogManager.getLogger(LoggerFilter.class.getName());

    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        chain.doFilter(req, res);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        String path = ServletUtility.buildPath(req);
        String method = req.getMethod();
        HttpSession session = req.getSession(false);
        String user = "Not logged";
        if (session != null)
            user = (String) session.getAttribute("user");

        int resStatus = res.getStatus();
        String logMessage = String.format(
                "Method: %s, Path: %s, Response Status: %d, Time elapsed: %dms., User: %s",
                method, path, resStatus, executionTime, user);

        LOGGER.info(logMessage);
    }
}