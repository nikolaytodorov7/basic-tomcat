package server.parser;

import java.util.ArrayList;
import java.util.List;

public class FilterMapping {
    public String name;
    public List<String> urlPatterns = new ArrayList<>();
    public List<String> servletNames = new ArrayList<>();

    public FilterMapping(String name) {
        this.name = name;
    }
}
