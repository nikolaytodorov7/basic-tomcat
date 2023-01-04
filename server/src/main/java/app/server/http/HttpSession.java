package app.server.http;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HttpSession {
    private String sessionId;
    private Map<String, Object> attributes = new HashMap<>();
    private ServletContext servletContext;
    private long creationTime;
    boolean isNew = true;

    public HttpSession(ServletContext servletContext) {
        this.servletContext = servletContext;
        sessionId = generateSessionId();
        servletContext.sessions.put(sessionId, this);
        creationTime = System.currentTimeMillis();
    }

    public Object getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    public void setAttribute(String user, Object value) {
        attributes.put(user, value);
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getId() {
        return sessionId;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void invalidate() {
        servletContext.sessions.remove(sessionId);
    }

    private String generateSessionId() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ignored) {
        } // ignored because exception is thrown if no Provider supports implementation for the specified algorithm. SHA-1 is supported.

        Random rand = new Random();
        int randomNumber = rand.nextInt(1_000_000, Integer.MAX_VALUE);

        md.update(String.valueOf(randomNumber).getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String sessionId = byteArrayToHexString(digest);
        if (servletContext.sessions.containsKey(sessionId))
            sessionId = generateSessionId();

        return sessionId;
    }

    private static String byteArrayToHexString(byte[] digest) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(Integer.toHexString(0xFF & b));
        }

        return hexString.toString();
    }
}
