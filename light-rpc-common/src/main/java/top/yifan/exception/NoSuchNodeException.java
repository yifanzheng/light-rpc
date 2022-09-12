package top.yifan.exception;

/**
 * NoSuchNodeException
 *
 * @author sz7v
 */
public class NoSuchNodeException extends RuntimeException {

    private static final long serialVersionUID = -5659584056093927091L;

    public NoSuchNodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
