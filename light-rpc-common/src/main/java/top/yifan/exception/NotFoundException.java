package top.yifan.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * NotFoundException
 */
public class NotFoundException extends AbstractThrowableProblem {

    private static final long serialVersionUID = -8550364743185289498L;

    public NotFoundException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, message, Status.NOT_FOUND);
    }

}
