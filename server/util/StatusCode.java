package util;

public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found");

    public final int CODE;
    public final String MESSAGE;

    StatusCode(int code, String message) {
        this.CODE = code;
        this.MESSAGE = message;
    }
}
