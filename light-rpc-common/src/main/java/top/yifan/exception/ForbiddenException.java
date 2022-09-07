package top.yifan.exception;


import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * ForbiddenException
 */
public class ForbiddenException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 7759924308548055273L;

    public ForbiddenException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, message, Status.FORBIDDEN);
    }
}
