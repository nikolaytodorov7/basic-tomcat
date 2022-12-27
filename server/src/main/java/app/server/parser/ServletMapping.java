package app.server.parser;

public class ServletMapping {
    public String servletName;
    public String urlPattern;

    public ServletMapping(String servletName, String urlPattern) {
        this.servletName = servletName;
        this.urlPattern = urlPattern;
    }
}
