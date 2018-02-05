package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class RaptorFrameworkException extends RaptorAbstractException {

    public RaptorFrameworkException() {
        super(RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public RaptorFrameworkException(RaptorMessage raptorMessage) {
        super(raptorMessage);
    }

    public RaptorFrameworkException(String message) {
        super(message, RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public RaptorFrameworkException(String message, RaptorMessage raptorMessage) {
        super(message, raptorMessage);
    }

    public RaptorFrameworkException(String message, Throwable cause) {
        super(message, cause, RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public RaptorFrameworkException(String message, Throwable cause, RaptorMessage raptorMessage) {
        super(message, cause, raptorMessage);
    }

    public RaptorFrameworkException(Throwable cause) {
        super(cause, RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public RaptorFrameworkException(Throwable cause, RaptorMessage raptorMessage) {
        super(cause, raptorMessage);
    }

}
