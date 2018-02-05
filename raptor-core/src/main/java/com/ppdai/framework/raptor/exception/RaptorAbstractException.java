package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import org.apache.commons.lang3.StringUtils;

public abstract class RaptorAbstractException extends RuntimeException {

    protected RaptorMessage raptorMessage = RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR;

    public RaptorAbstractException() {
        super();
    }

    public RaptorAbstractException(RaptorMessage raptorMessage) {
        super();
        this.raptorMessage = raptorMessage;
    }

    public RaptorAbstractException(String message) {
        super(message);
    }

    public RaptorAbstractException(String message, RaptorMessage raptorMessage) {
        super(message);
        this.raptorMessage = raptorMessage;
    }

    public RaptorAbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public RaptorAbstractException(String message, Throwable cause, RaptorMessage raptorMessage) {
        super(message, cause);
        this.raptorMessage = raptorMessage;
    }

    public RaptorAbstractException(Throwable cause) {
        super(cause);
    }

    public RaptorAbstractException(Throwable cause, RaptorMessage raptorMessage) {
        super(cause);
        this.raptorMessage = raptorMessage;
    }


    @Override
    public String getMessage() {
        String message = getOriginMessage();
        return "error_message: " + message + ", status: " + raptorMessage.getStatus() + ", error_code: " + raptorMessage.getErrorCode();
    }

    public String getOriginMessage() {
        if (StringUtils.isBlank(super.getMessage())) {
            return raptorMessage.getMessage();
        }
        return super.getMessage();
    }

    public int getStatus() {
        return raptorMessage != null ? raptorMessage.getStatus() : 0;
    }

    public int getErrorCode() {
        return raptorMessage != null ? raptorMessage.getErrorCode() : 0;
    }

    public RaptorMessage getRaptorMessage() {
        return raptorMessage;
    }
}
