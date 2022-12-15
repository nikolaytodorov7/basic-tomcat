package parser;

public class FilterMapping {
    public String filterName;
    public String urlPattern;
    public String servletName;

    public FilterMapping(String filterName, String urlPattern, String servletName) {
        this.filterName = filterName;
        this.urlPattern = urlPattern;
        this.servletName = servletName;
    }
}
