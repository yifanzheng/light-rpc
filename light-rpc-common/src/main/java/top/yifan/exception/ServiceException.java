package top.yifan.exception;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -6159843199613859968L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
