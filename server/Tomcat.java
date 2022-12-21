
import http.HttpServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import parser.Configuration;
import parser.WebXMLParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Tomcat {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        WebXMLParser parser = new WebXMLParser();
        parser.parse("src\\webapp\\web.xml");
        Configuration configuration = parser.getConfiguration();
        startServer(configuration);
    }

    private static void startServer(Configuration configuration) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse("src/server/servlet.xml");
        String docBase = getDocBase(document);
        String contextPath = getContextPath(document);
        HttpServer.start(configuration, contextPath, docBase);
    }

    private static String getContextPath(Document document) {
        Node node = document.getElementsByTagName("Context").item(0);
        Element element = (Element) node;
        return '/' + element.getAttribute("path");
    }

    private static String getDocBase(Document document) {
        Node node = document.getElementsByTagName("Context").item(0);
        Element element = (Element) node;
        return '/' + element.getAttribute("docBase");
    }
}
