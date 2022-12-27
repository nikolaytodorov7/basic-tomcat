package app.webapp.filter;

import app.webapp.model.StatusMessage;
import app.webapp.util.ServletUtility;
import app.server.exception.ServletException;
import app.server.http.*;

import java.io.IOException;

public class AuthenticationFilter extends HttpFilter {
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            ServletUtility.writeAsJson(resp, msg);
            return;
        }

        chain.doFilter(req, resp);
    }
}
