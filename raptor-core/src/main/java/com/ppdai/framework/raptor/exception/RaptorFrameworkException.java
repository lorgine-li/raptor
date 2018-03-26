package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

import java.util.Map;

public class RaptorFrameworkException extends AbstractRaptorException {

    public RaptorFrameworkException(String message) {
        super(RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR_CODE, message, null, null);
    }

    public RaptorFrameworkException(String message, Throwable cause) {
        super(RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR_CODE, message, null, cause);
    }

    public RaptorFrameworkException(int code, String message, Throwable cause) {
        super(code, message, null, cause);
    }

    public RaptorFrameworkException(int code, String message, Map<String, String> attachments, Throwable cause) {
        super(code, message, attachments, cause);
    }
}
