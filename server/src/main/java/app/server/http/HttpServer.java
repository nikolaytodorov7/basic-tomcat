package app.server.http;

import app.server.parser.Configuration;
import app.server.parser.WebXMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final int DEFAULT_THREADS = 5;
    private static final int DEFAULT_PORT = 80;
    private static int threadsNumber = DEFAULT_THREADS;
    private static int port = DEFAULT_PORT;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(threadsNumber);
    private static ServletContext servletContext;
    private static String docBase;
    private static String contextPath;

    public static void start() throws IOException {
        setDocAndContext();
        WebXMLParser parser = new WebXMLParser(docBase);
        parser.parse();
        Configuration configuration = Configuration.getConfiguration();

        servletContext = new ServletContext(configuration);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(ServerTask.runTask(socket, contextPath, docBase, servletContext));
            }
        }
    }

    private static String getContextPath(Document document) {
        Node node = document.getElementsByTagName("Context").item(0);
        Element element = (Element) node;
        return '/' + element.getAttribute("path");
    }

    private static String getDocBase(Document document) {
        Node node = document.getElementsByTagName("Context").item(0);
        Element element = (Element) node;
        return element.getAttribute("docBase");
    }

    private static void setDocAndContext() throws IOException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("C:\\Work\\IdeaProjects\\tomcat-clone\\server\\src\\main\\webapp\\WEB-INF\\server.xml");
            docBase = getDocBase(document);
            contextPath = getContextPath(document);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("No server XML Found!");
        }
    }
}
