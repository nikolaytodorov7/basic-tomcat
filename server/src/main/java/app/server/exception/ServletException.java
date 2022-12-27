package app.server.exception;

public class ServletException extends Exception {
    private Throwable cause;

    public ServletException() {
        super();
    }

    public ServletException(String message) {
        super(message);
    }

    public ServletException(String message, Throwable cause) {
        super(message, cause);
        this.cause = cause;
    }

    public ServletException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
