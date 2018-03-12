package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class RaptorServiceException extends RaptorAbstractException {


    public RaptorServiceException(String message) {
        super(new ErrorMessage(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE, message), null);
    }

    public RaptorServiceException(String message, Throwable cause) {
        super(new ErrorMessage(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE, message), cause);
    }

    public RaptorServiceException(int code, String message, Throwable cause) {
        super(new ErrorMessage(code, message), cause);
    }

    public RaptorServiceException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }


}
