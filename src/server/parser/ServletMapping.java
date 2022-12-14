package server.parser;

import java.util.ArrayList;
import java.util.List;

public class ServletMapping {
    public String name;
    public List<String> urlPatterns = new ArrayList<>();

    public ServletMapping(String name) {
        this.name = name;
    }
}
