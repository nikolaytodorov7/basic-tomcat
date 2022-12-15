package filter;

import exception.ServletException;
import http.*;
import model.StatusMessage;

import java.io.IOException;

import static util.ServletUtility.writeAsJson;

public class AuthenticationFilter extends HttpFilter {
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            writeAsJson(resp, msg);
            return;
        }

        chain.doFilter(req, resp);
    }
}
