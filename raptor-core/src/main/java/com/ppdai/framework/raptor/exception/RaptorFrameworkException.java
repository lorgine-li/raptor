package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class RaptorFrameworkException extends RaptorAbstractException {

    public RaptorFrameworkException(String message) {
        super(new ErrorMessage(RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR_CODE, message), null);
    }

    public RaptorFrameworkException(String message, Throwable cause) {
        super(new ErrorMessage(RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR_CODE, message), cause);
    }

    public RaptorFrameworkException(int code, String message, Throwable cause) {
        super(new ErrorMessage(code, message), cause);
    }

    public RaptorFrameworkException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
