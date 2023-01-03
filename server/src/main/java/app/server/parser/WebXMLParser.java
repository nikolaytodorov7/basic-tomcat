package app.server.parser;

import app.server.http.HttpFilter;
import app.server.http.HttpServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WebXMLParser {
    private Configuration configuration = Configuration.getConfiguration();
    private Document document;
    private boolean parsed = false;
    private URLClassLoader loader;
    private String docBase;

    public WebXMLParser(String docBase) {
        this.docBase = docBase;
        createClassLoader();
    }

    private void createClassLoader() {
        String classesPath = docBase + "\\webapp\\target\\webapp-1.0-SNAPSHOT\\WEB-INF\\classes";
        String resourcesPath = docBase + "\\webapp\\src\\resources";
        String libPath = docBase + "\\webapp\\target\\webapp-1.0-SNAPSHOT\\WEB-INF\\lib";
        URL[] urls = getUrls(classesPath, resourcesPath, libPath);
        loader = URLClassLoader.newInstance(urls);
    }

    public URL[] getUrls(String classesPath, String resourcesPath, String libPath) {
        File dir = new File(classesPath);
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(dir.toURI().toURL());
            urls.add(new File(resourcesPath).toURI().toURL());
            File file = new File(libPath);
            Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach((t) -> {
                try {
                    urls.add(t.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e); //todo
                }
            });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        URL[] urlArr = new URL[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlArr[i] = urls.get(i);
        }

        return urlArr;
    }

    public void parse() {
        String path = docBase + "\\webapp\\target\\webapp-1.0-SNAPSHOT\\WEB-INF\\web.xml";
        if (parsed) {
            String msg = String.format("Xml file '%s' has already been parsed!", path);
            throw new RuntimeException(msg);
        }

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(path);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot create DocumentBuilder. It will not satisfy the configuration requested!", e);
        } catch (IOException | SAXException e) {
            throw new RuntimeException("Parse error has occurred!", e);
        }

        processDocument();
    }

    private void processDocument() {
        if (parsed)
            throw new RuntimeException("Document has already been processed!");

        NodeList filterNodes = document.getElementsByTagName("filter");
        parseFilters(filterNodes);
        NodeList filterMappingNodes = document.getElementsByTagName("filter-mapping");
        parseFilterMappings(filterMappingNodes);
        NodeList servletNodes = document.getElementsByTagName("servlet");
        parseServlets(servletNodes);
        NodeList servletMappingNodes = document.getElementsByTagName("servlet-mapping");
        parseServletMappings(servletMappingNodes);
    }

    private void parseFilters(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String filterName = getElementTextContentByTagName(element, "filter-name");
            if (filterName == null)
                throw new IllegalArgumentException("Filter name not found!");

            if (configuration.filters.containsKey(filterName))
                return;

            String filterClass = getElementTextContentByTagName(element, "filter-class");
            if (filterClass == null)
                throw new IllegalArgumentException("Filter class not found!");

            try {
                Class<?> clazz = loader.loadClass(filterClass);
                Constructor<?> constructor = clazz.getConstructor();
                HttpFilter filter = (HttpFilter) constructor.newInstance();
                configuration.filters.put(filterName, filter);
            } catch (ClassNotFoundException e) {
                String msg = String.format("Filter class with name: '%s' not found!", filterClass);
                throw new RuntimeException(msg, e);
            } catch (NoSuchMethodException e) {
                String msg = String.format("Constructor for filter: '%s' not found!", filterName);
                throw new RuntimeException(msg, e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                String msg = String.format("Instance of class: '%s' not found!", filterClass);
                throw new RuntimeException(msg, e);
            }
        }
    }

    private void parseFilterMappings(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String filterName = getElementTextContentByTagName(element, "filter-name");
            if (filterName == null)
                throw new IllegalArgumentException("Filter name not found!");

            String urlPattern = getElementTextContentByTagName(element, "url-pattern");
            String servletName = getElementTextContentByTagName(element, "servlet-name");
            if (urlPattern == null && servletName == null)
                throw new IllegalArgumentException("Filter must have url pattern or servlet name!");

            configuration.addFilterMapping(filterName, urlPattern, servletName);
        }
    }

    private void parseServlets(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String servletName = getElementTextContentByTagName(element, "servlet-name");
            if (servletName == null)
                throw new IllegalArgumentException("Servlet name not found!");

            if (configuration.servlets.containsKey(servletName))
                return;

            String servletClass = getElementTextContentByTagName(element, "servlet-class");
            if (servletClass == null)
                throw new IllegalArgumentException("Servlet class not found!");

            try {
                Class<?> clazz = loader.loadClass(servletClass);
                Constructor<?> constructor = clazz.getConstructor();
                HttpServlet servlet = (HttpServlet) constructor.newInstance();
                configuration.servlets.put(servletName, servlet);
            } catch (ClassNotFoundException e) {
                String msg = String.format("Servlet class with name: '%s' not found!", servletClass);
                throw new RuntimeException(msg, e);
            } catch (NoSuchMethodException e) {
                String msg = String.format("Constructor for servlet: '%s' not found!", servletName);
                throw new RuntimeException(msg, e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                String msg = String.format("Instance of class: '%s' not found!", servletClass);
                throw new RuntimeException(msg, e);
            }
        }
    }

    private void parseServletMappings(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String servletName = getElementTextContentByTagName(element, "servlet-name");
            if (servletName == null)
                throw new IllegalArgumentException("Servlet name not found!");

            String urlPattern = getElementTextContentByTagName(element, "url-pattern");
            configuration.addServletMapping(servletName, urlPattern);
        }
    }

    private static String getElementTextContentByTagName(Element element, String tagName) {
        Node node = element.getElementsByTagName(tagName).item(0);
        return node == null ? null : node.getTextContent();
    }
}
