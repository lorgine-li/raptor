package com.ppdai.framework.raptor.rpc;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public interface Request {

    String getInterfaceName();

    String getMethodName();

    Object getArgument();

    String getReturnType();

    Map<String, String> getAttachments();

    void setAttachment(String name, String value);

    String getRequestId();

}
