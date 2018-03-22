package com.ppdai.framework.raptor.util;

import com.ppdai.framework.raptor.exception.AbstractRaptorException;
import com.ppdai.framework.raptor.exception.RaptorBizException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtil {

    public static boolean isBizException(Throwable t) {
        return t instanceof RaptorBizException;
    }

    public static boolean isRaptorException(Throwable t) {
        return t instanceof AbstractRaptorException;
    }

}
