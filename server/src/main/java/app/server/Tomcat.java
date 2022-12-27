package app.server;

import app.server.http.HttpServer;
import app.server.parser.Configuration;
import app.server.parser.WebXMLParser;

public class Tomcat {
    public static void main(String[] args) throws Exception {
        WebXMLParser parser = new WebXMLParser("C:\\Work\\IdeaProjects\\tomcat-clone\\webapp\\target\\webapp-1.0-SNAPSHOT\\WEB-INF\\classes");
        parser.parse("C:\\Work\\IdeaProjects\\tomcat-clone\\webapp\\target\\webapp-1.0-SNAPSHOT\\WEB-INF\\web.xml");
        Configuration configuration = Configuration.getConfiguration();
        HttpServer.start(configuration);
    }
}
