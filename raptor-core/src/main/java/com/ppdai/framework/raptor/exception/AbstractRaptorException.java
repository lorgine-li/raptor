package com.ppdai.framework.raptor.exception;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractRaptorException extends RuntimeException {

    protected ErrorMessage errorMessage;

    public AbstractRaptorException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        String message = getOriginMessage();
        return "error_message: " + message + ", error_code: " + errorMessage.getCode();
    }

    public String getOriginMessage() {
        if (StringUtils.isBlank(super.getMessage())) {
            return errorMessage.getMessage();
        }
        return super.getMessage();
    }

    public int getErrorCode() {
        return errorMessage != null ? errorMessage.getCode() : 0;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
