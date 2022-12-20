
import http.HttpServer;
import parser.Configuration;
import parser.WebXMLParser;

import java.io.IOException;

public class Tomcat {
    public static void main(String[] args) throws IOException {
        WebXMLParser parser = new WebXMLParser();
        parser.parse("src\\webapp\\web.xml");
        Configuration configuration = parser.getConfiguration();
        HttpServer.start(configuration);
    }
}
