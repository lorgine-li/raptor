package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class RaptorBizException extends AbstractRaptorException {

    public RaptorBizException(String message) {
        super(new ErrorMessage(RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE, message), null);
    }

    public RaptorBizException(String message, Throwable cause) {
        super(new ErrorMessage(RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE, message), cause);
    }

    public RaptorBizException(int code, String message, Throwable cause) {
        super(new ErrorMessage(code, message), cause);
    }

    public RaptorBizException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
