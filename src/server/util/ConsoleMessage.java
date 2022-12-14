package server.util;

import java.time.LocalDateTime;

public class ConsoleMessage {
    public static void printOK(String method, String path, String userAgent) {
        System.out.printf("[%s]  \"%s %s\" \"%s\"\n", LocalDateTime.now(), method, path, userAgent);
    }

    public static void printNotFound(String method, String path) {
        StatusCode notFound = StatusCode.NOT_FOUND;
        int code = notFound.CODE;
        String statusMessage = notFound.MESSAGE;
        System.out.printf("[%s]  \"%s %s\" Error (%d): \"%s\"\n", LocalDateTime.now(), method, path, code, statusMessage);
    }

    public static void printBadRequest() {
        StatusCode badRequest = StatusCode.BAD_REQUEST;
        int code = badRequest.CODE;
        String statusMessage = badRequest.MESSAGE;
        System.out.printf("[%s]  Error (%d): \"%s\"\n", LocalDateTime.now(), code, statusMessage);
    }
}