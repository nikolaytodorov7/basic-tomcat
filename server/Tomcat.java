
import http.HttpServer;
import parser.Configuration;
import parser.WebXMLParser;

import java.io.IOException;

public class Tomcat {
    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        WebXMLParser parser = new WebXMLParser();
        parser.parse("C:\\Work\\IdeaProjects\\basic-tomcat\\src\\webapp\\web.xml"); // todo remove hardcode
        Configuration configuration = parser.getConfiguration();
        HttpServer.start(configuration);
    }
}
