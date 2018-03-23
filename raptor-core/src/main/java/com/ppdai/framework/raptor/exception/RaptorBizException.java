package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

import java.util.Map;

public class RaptorBizException extends AbstractRaptorException {

    public RaptorBizException(String message) {
        super(RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE, message, null, null);
    }

    public RaptorBizException(String message, Throwable cause) {
        super(RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE, message, null, cause);
    }

    public RaptorBizException(int code, String message, Throwable cause) {
        super(code, message, null, cause);
    }

    public RaptorBizException(int code, String message, Map<String, String> attachments, Throwable cause) {
        super(code, message, attachments, cause);
    }

}
