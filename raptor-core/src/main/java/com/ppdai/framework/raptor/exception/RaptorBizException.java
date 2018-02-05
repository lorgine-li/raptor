package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class RaptorBizException extends RaptorAbstractException {

    public RaptorBizException() {
        super(RaptorMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public RaptorBizException(RaptorMessage raptorMessage) {
        super(raptorMessage);
    }

    public RaptorBizException(String message) {
        super(message, RaptorMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public RaptorBizException(String message, RaptorMessage raptorMessage) {
        super(message, raptorMessage);
    }

    public RaptorBizException(String message, Throwable cause) {
        super(message, cause, RaptorMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public RaptorBizException(String message, Throwable cause, RaptorMessage raptorMessage) {
        super(message, cause, raptorMessage);
    }

    public RaptorBizException(Throwable cause) {
        super(cause, RaptorMessageConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public RaptorBizException(Throwable cause, RaptorMessage raptorMessage) {
        super(cause, raptorMessage);
    }
}
