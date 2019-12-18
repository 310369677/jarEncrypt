package com.yang.encrypt;

/**
 * 描述:字节转换int类型异常
 * 公司:jwell
 * 作者:杨川东
 * 日期:18-4-19
 */
public class Byte2NumberException extends RuntimeException {

    private static final long serialVersionUID = 1413388715735384284L;

    public Byte2NumberException() {
    }

    public Byte2NumberException(String message) {
        super(message);
    }

    public Byte2NumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public Byte2NumberException(Throwable cause) {
        super(cause);
    }

    public Byte2NumberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
