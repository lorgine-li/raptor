package com.ppdai.framework.raptor.common;

import com.ppdai.framework.raptor.util.NetUtils;

import java.net.InetAddress;

/**
 * 根据机器IP获取工作进程Id,如果线上机器的IP二进制表示的最后10位不重复,建议使用此种方式
 * ,列如机器的IP为192.168.1.108,二进制表示:11000000 10101000 00000001 01101100
 * ,截取最后10位 01 01101100,转为十进制364,设置workerId为364.
 */
public class IpIdGenerator implements IdGenerator {

    private final CommonSelfIdGenerator commonSelfIdGenerator = new CommonSelfIdGenerator();

    public IpIdGenerator() {
        initWorkerId();
    }

    private void initWorkerId() {
        InetAddress address = NetUtils.getLocalAddress();
        byte[] ipAddressByteArray = address.getAddress();
        commonSelfIdGenerator.setWorkerId((long) (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE) + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF)));
    }

    @Override
    public long generateId() {
        return commonSelfIdGenerator.generateId();
    }
}
