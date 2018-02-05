package com.ppdai.framework.raptor.rpc;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public interface Request {

    String getInterfaceName();

    String getMethodName();

    Object getArguments();

    Map<String, String> getAttachments();

    void setAttachment(String name, String value);

    String getRequestId();

}
