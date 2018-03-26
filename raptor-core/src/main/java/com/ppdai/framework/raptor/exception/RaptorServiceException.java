package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

import java.util.Map;

public class RaptorServiceException extends AbstractRaptorException {

    public RaptorServiceException(String message) {
        super(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE, message, null, null);
    }

    public RaptorServiceException(String message, Throwable cause) {
        super(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE, message, null, cause);
    }

    public RaptorServiceException(int code, String message, Throwable cause) {
        super(code, message, null, cause);
    }

    public RaptorServiceException(int code, String message, Map<String, String> attachments, Throwable cause) {
        super(code, message, attachments, cause);
    }

}
