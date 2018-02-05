package com.ppdai.framework.raptor.util;


import com.ppdai.framework.raptor.common.IpIdGenerator;

public class RequestIdGenerator {

    private static IpIdGenerator ipIdGenerator = new IpIdGenerator();

    /**
     * 获取 requestId
     *
     * @return
     */
    public static String getRequestId() {
        return String.valueOf(ipIdGenerator.generateId());
    }
}
