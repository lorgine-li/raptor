package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorMessageConstant;

public class HttpErrorConvertor {

    public static ErrorMessage getErrorMessage(Exception e) {
        if (e == null) {
            return new ErrorMessage(RaptorMessageConstant.SUCCESS, "SUCCESS");
        }
        if (e instanceof RaptorAbstractException) {
            return ((RaptorAbstractException) e).getErrorMessage();
        }
        return new ErrorMessage(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE, e.getMessage());
    }

    public static int getHttpStatusCode(Exception e) {
        if (e == null) {
            return 200;
        }
        if (e instanceof RaptorAbstractException) {
            int code = ((RaptorAbstractException) e).getErrorMessage().getCode();
            return getHttpStatusCode(code);
        }
        return getDefaultErrorHttpStatusCode();
    }

    public static int getHttpStatusCode(ErrorMessage message) {
        if (message != null) {
            return getHttpStatusCode(message.getCode());
        }
        return getDefaultErrorHttpStatusCode();
    }

    public static int getHttpStatusCode(int raptorCode) {
        if (raptorCode == RaptorMessageConstant.SUCCESS) {
            return 200;
        }
        //TODO 区分不同的异常对应http不同的statusCode
        //定义419为raptor异常
        return getDefaultErrorHttpStatusCode();
    }

    public static int getDefaultErrorHttpStatusCode() {
        //定义419为raptor异常
        return 419;
    }
}
