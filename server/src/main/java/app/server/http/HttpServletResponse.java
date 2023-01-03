package app.server.http;

import app.server.util.StatusCode;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    private static final String DEFAULT_FILE_INDEX_HTML = "index.html";
    private static final String HTML_LINKS = "<a href=%s/%s>%s</a>";
    private static final String HTML_LINE_BREAK = "<br/>";
    public static final int SC_OK = 200;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_NOT_FOUND = 404;
    private static String docBase = "";

    private Map<String, String> headers = new HashMap<>();
    private PrintWriter printWriter;
    private OutputStream outputStream;
    private InputStream inBody;
    private String strBody;
    private String protocol;
    private StatusCode statusCode;
    private int status = SC_OK;
    boolean staticResponseFailed = false;

    public HttpServletResponse(Socket socket) {
        try {
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpServletResponse(String protocol) {
        this(protocol, StatusCode.OK);
    }

    private HttpServletResponse(String protocol, StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
    }

    void prepareStaticResponse(String protocol, String currentPath) throws IOException {
        this.protocol = protocol;
        Path path = Path.of(docBase + currentPath);
        File target = path.toFile();
        if (!target.exists()) {
            sendError(String.valueOf(path));
            staticResponseFailed = true;
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (target.isDirectory())
            prepareDirectoryResponse(path, target, sb);

        prepareFileResponse(protocol, target);
    }

    private void prepareDirectoryResponse(Path path, File target, StringBuilder sb) throws IOException {
        File[] files = target.listFiles();
        for (File file : files) {
            if (file.getName().equals(DEFAULT_FILE_INDEX_HTML))
                prepareFileResponse(protocol, file);

            String fileName = file.getName();
            String formatted = String.format(HTML_LINKS, Path.of(docBase).equals(path) ? "." : path.getFileName(), fileName, fileName);
            sb.append(formatted).append(HTML_LINE_BREAK);
        }

        sb.setLength(sb.length() - HTML_LINE_BREAK.length());
        prepareStringResponse(protocol, sb.toString());
    }

    private void prepareStringResponse(String protocol, String httpResponse) {
        HttpServletResponse response = new HttpServletResponse(protocol);
        headers.put("Content-type", "text/plain");
        response.strBody = httpResponse;
    }

    private void prepareFileResponse(String protocol, File file) throws IOException {
        this.protocol = protocol;
        inBody = new FileInputStream(file);
        processHeaders(file);
    }

    private void processHeaders(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        headers.put("Content-type", contentType);
        String contentLength = String.valueOf(file.length());
        headers.put("Content-length", contentLength);
        String date = String.valueOf(LocalDateTime.now());
        headers.put("Date", date);
        String lastModified = String.valueOf(Files.getLastModifiedTime(file.toPath()));
        headers.put("Last-modified", lastModified);
    }

    public void send() throws IOException {
        writeResponse(printWriter);
        if (strBody != null)
            printWriter.println(strBody);
        else
            inBody.transferTo(outputStream);
    }

    private void writeResponse(PrintWriter writer) {
        writer.println(protocol + " " + statusCode.CODE + " " + statusCode.MESSAGE);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            writer.println(key + ": " + value);
        }

        writer.println();
        writer.flush();
    }


    public OutputStream getOutputStream() {
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setDocBase(String base) {
        docBase = base;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int statusCode) {
        this.status = switch (statusCode) {
            case 200 -> SC_OK;
            case 400 -> SC_BAD_REQUEST;
            case 404 -> SC_NOT_FOUND;
            default ->
                    throw new IllegalArgumentException(String.format("Illegal status code '%d' provided!", statusCode));
        };
    }

    public void sendError(String path) {
        setHeader("Content-type", "text/plain");
        String msg = String.format("HTTP Status 404 – Not Found\nThe requested resource [%s] is not available", path);
        printWriter.println(msg);
        printWriter.flush();
    }

    public void addCookie(Cookie cookie) {
        StringBuilder cookieBuilder = new StringBuilder();
        String key = "Set-Cookie";
        cookieBuilder.append(cookie.getName()).append('=').append(cookie.getValue());

        headers.put("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
    }
}
