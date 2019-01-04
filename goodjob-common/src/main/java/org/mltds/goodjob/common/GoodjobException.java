package org.mltds.goodjob.common;

/**
 * @author sunyi 2018/11/21.
 */
public class GoodjobException extends RuntimeException {

    public GoodjobException() {
        super();
    }

    public GoodjobException(String message) {
        super(message);
    }

    public GoodjobException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoodjobException(Throwable cause) {
        super(cause);
    }

    protected GoodjobException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}