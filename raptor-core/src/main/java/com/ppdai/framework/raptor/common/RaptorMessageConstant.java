package com.ppdai.framework.raptor.common;

import com.ppdai.framework.raptor.exception.ErrorMessage;

public class RaptorMessageConstant {
    public static final int SUCCESS = 0;
    /**
     * service error
     */
    public static final int SERVICE_DEFAULT_ERROR_CODE = 10001;
    public static final int SERVICE_NOTFOUND_ERROR_CODE = 10101;
    public static final int SERVICE_REDIRECT_ERROR_CODE = 10300;
    public static final int SERVICE_UNKNOW_ERROR_CODE = 10000;

    /**
     * framework error
     */
    public static final int FRAMEWORK_DEFAULT_ERROR_CODE = 20001;

    /**
     *  biz exception
     */
    public static final int BIZ_DEFAULT_ERROR_CODE = 30001;

    public static final ErrorMessage SERVICE_DEFAULT_ERROR = new ErrorMessage(SERVICE_DEFAULT_ERROR_CODE, "service error");
    public static final ErrorMessage SERVICE_UNFOUND = new ErrorMessage(SERVICE_NOTFOUND_ERROR_CODE, "service not found");

    public static final ErrorMessage FRAMEWORK_DEFAULT_ERROR = new ErrorMessage(FRAMEWORK_DEFAULT_ERROR_CODE, "framework default error");

    public static final ErrorMessage BIZ_DEFAULT_EXCEPTION = new ErrorMessage(BIZ_DEFAULT_ERROR_CODE, "provider error");

    private RaptorMessageConstant() {
    }
}
