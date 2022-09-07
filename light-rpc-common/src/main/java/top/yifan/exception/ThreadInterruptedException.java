package top.yifan.exception;

/**
 * 主要用于将InterruptedException转换为非受检的异常
 */
public class ThreadInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 154311660173086023L;

    public ThreadInterruptedException(InterruptedException exception) {
        super(exception);
    }

}