package app.server.http;

import java.util.HashMap;
import java.util.Map;

public class Cookie {
    private final String name;
    private String value;
    private Map<String, String> attributes = new HashMap<>();

    public Cookie(String name, String value) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Cookie name is blank!");

        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setValue(String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }

    public void setMaxAge(int maxAge) {
        attributes.put("Max-age", maxAge < 0 ? null : String.valueOf(maxAge));
    }

    public int getMaxAge() {
        String maxAge = attributes.get("Max-age");
        return maxAge == null ? -1 : Integer.parseInt(maxAge);
    }

    public void setPath(String uri) {
        attributes.put("Path", uri);
    }

    public String getPath() {
        return attributes.get("Path");
    }

    public void setDomain(String domain) {
        attributes.put("Domain", domain != null ? domain.toLowerCase() : null);
    }

    public String getDomain() {
        return attributes.get("Domain");
    }

    public void setSecure(boolean flag) {
        attributes.put("Secure", String.valueOf(flag));
    }

    public boolean getSecure() {
        return Boolean.parseBoolean(attributes.get("Secure"));
    }

    public void setHttpOnly(boolean httpOnly) {
        attributes.put("HttpOnly", String.valueOf(httpOnly));
    }

    public boolean isHttpOnly() {
        return Boolean.parseBoolean(attributes.get("HttpOnly"));
    }
}
