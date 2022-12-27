package app.webapp.servlet;

import app.webapp.mapper.CommentMapper;
import app.webapp.model.StatusMessage;
import app.server.http.HttpServlet;
import app.server.http.HttpServletRequest;
import app.server.http.HttpServletResponse;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import static app.webapp.util.ServletUtility.*;

public class CommentServlet extends HttpServlet {
    private static CommentMapper mapper;
    private static final Pattern COMMENTS_OF_POST_WITH_ID = Pattern.compile("/comments\\?postId=\\d+");

    public void init() {
        try {
            Properties properties = Resources.getResourceAsProperties("jdbc.properties");
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in, properties);
            mapper = new CommentMapper(factory);
        } catch (IOException e) {
            System.err.println("Properties error!\n" + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = buildPath(req);
        if (!COMMENTS_OF_POST_WITH_ID.matcher(path).matches()) {
            StatusMessage msg = new StatusMessage(400, "Invalid link.");
            writeAsJson(resp, msg);
        }

        String[] split = path.split("=");
        int id = parseInt(split[1]);
        if (id > 0)
            mapper.getCommentsByPostId(id).forEach((p) -> writeAsJson(resp, p));
        else {
            StatusMessage msg = new StatusMessage(400, "Invalid id given in link.");
            writeAsJson(resp, msg);
        }
    }
}