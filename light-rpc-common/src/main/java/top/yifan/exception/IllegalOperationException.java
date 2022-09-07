package top.yifan.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 非法操作异常，通常来自客户端不允许的操作请求
 */
public class IllegalOperationException extends AbstractThrowableProblem {

    private static final long serialVersionUID = -5066281737866843713L;

    public IllegalOperationException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, message, Status.METHOD_NOT_ALLOWED);
    }

    public IllegalOperationException(String message, Status status) {
        super(ErrorConstants.DEFAULT_TYPE, message, status);
    }

}