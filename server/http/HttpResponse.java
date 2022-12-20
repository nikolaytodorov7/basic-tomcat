package http;

import util.StatusCode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final String DEFAULT_FILE_INDEX_HTML = "index.html";
    private static final String HTML_LINKS = "<a href=%s/%s>%s</a>";
    private static final String HTML_LINE_BREAK = "<br/>";
    private final Map<String, String> headers = new HashMap<>();
    private InputStream inBody;
    private String strBody;
    private String protocol;
    private StatusCode statusCode;

    private HttpResponse(String protocol) {
        this(protocol, StatusCode.OK);
    }

    private HttpResponse(String protocol, StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
    }

    public static HttpResponse getResponse(PrintWriter writer, String protocol, String currentPath) throws IOException {
        String dir = "C:/";
        Path path = Path.of(dir + currentPath);
        File target = path.toFile();
        if (!target.exists()) {
            String msg = String.format("HTTP Status 404 – Not Found\nThe requested resource [%s] is not available", path);
            writer.println(msg);
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (target.isDirectory())
            return createResponseFromDirectory(protocol, dir, path, target, sb);

        return HttpResponse.fileResponse(protocol, target);
    }

    private static HttpResponse createResponseFromDirectory(String protocol, String dir, Path path, File target, StringBuilder sb) throws IOException {
        File[] files = target.listFiles();
        for (File file : files) {
            if (file.getName().equals(DEFAULT_FILE_INDEX_HTML))
                return HttpResponse.fileResponse(protocol, file);

            String fileName = file.getName();
            String formatted = String.format(HTML_LINKS, Path.of(dir).equals(path) ? "." : path.getFileName(), fileName, fileName);
            sb.append(formatted).append(HTML_LINE_BREAK);
        }

        sb.setLength(sb.length() - HTML_LINE_BREAK.length());
        return HttpResponse.stringResponse(protocol, sb.toString());
    }

    private static HttpResponse stringResponse(String protocol, String httpResponse) {
        HttpResponse response = new HttpResponse(protocol);
        response.strBody = httpResponse;
        return response;
    }

    private static HttpResponse fileResponse(String protocol, File file) throws IOException {
        HttpResponse response = new HttpResponse(protocol);
        response.inBody = new FileInputStream(file);
        processHeaders(file, response);
        return response;
    }

    private static void processHeaders(File file, HttpResponse response) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        response.headers.put("Content-type", contentType);
        String contentLength = String.valueOf(file.length());
        response.headers.put("Content-length", contentLength);
        String date = String.valueOf(LocalDateTime.now());
        response.headers.put("Date", date);
        String lastModified = String.valueOf(Files.getLastModifiedTime(file.toPath()));
        response.headers.put("Last-modified", lastModified);
    }

    public void send(PrintWriter writer, OutputStream outputStream) throws IOException {
        String response = prepareResponse();
        byte[] bytes = response.getBytes();
        writer.println(new String(bytes));
        if (strBody != null)
            stringToPrintWriter(writer);
        else
            inputStreamToPrintWriter(outputStream);
    }

    private void stringToPrintWriter(PrintWriter writer) {
        writer.println(new String(strBody.getBytes()));
    }

    private void inputStreamToPrintWriter(OutputStream out) throws IOException {
        byte[] bytes = new byte[2048];
        int n;
        while ((n = inBody.read(bytes)) != 1) {
            out.write(bytes, 0, n);
        }

        out.flush();
    }

    private String prepareResponse() {
        StringBuilder response = new StringBuilder();
        response.append(protocol).
                append(" ").
                append(statusCode.CODE).
                append(" ").
                append(statusCode.MESSAGE).
                append(System.lineSeparator());

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            response.append(key).append(": ")
                    .append(value).append(System.lineSeparator());
        }

        response.append(System.lineSeparator());
        return response.toString();
    }
}
