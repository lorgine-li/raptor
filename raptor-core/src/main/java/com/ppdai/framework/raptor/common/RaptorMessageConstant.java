package com.ppdai.framework.raptor.common;

public class RaptorMessageConstant {
    public static final int SUCCESS = 0;
    /**
     * service error
     */
    public static final int SERVICE_DEFAULT_ERROR_CODE = 10000;
    public static final int SERVICE_NOTFOUND_ERROR_CODE = 10001;
    public static final int SERVICE_UNKNOW_ERROR_CODE = 19999;

    /**
     * framework error
     */
    public static final int FRAMEWORK_DEFAULT_ERROR_CODE = 20000;

    /**
     * biz exception
     */
    public static final int BIZ_DEFAULT_ERROR_CODE = 30000;

    private RaptorMessageConstant() {
    }
}
