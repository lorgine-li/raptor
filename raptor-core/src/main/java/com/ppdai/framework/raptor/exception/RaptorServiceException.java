package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class RaptorServiceException extends RaptorAbstractException {

    public RaptorServiceException() {
        super(RaptorMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public RaptorServiceException(RaptorMessage raptorMessage) {
        super(raptorMessage);
    }

    public RaptorServiceException(String message) {
        super(message, RaptorMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public RaptorServiceException(String message, RaptorMessage raptorMessage) {
        super(message, raptorMessage);
    }

    public RaptorServiceException(String message, Throwable cause) {
        super(message, cause, RaptorMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public RaptorServiceException(String message, Throwable cause, RaptorMessage raptorMessage) {
        super(message, cause, raptorMessage);
    }

    public RaptorServiceException(Throwable cause) {
        super(cause, RaptorMessageConstant.SERVICE_DEFAULT_ERROR);
    }

    public RaptorServiceException(Throwable cause, RaptorMessage raptorMessage) {
        super(cause, raptorMessage);
    }

}
