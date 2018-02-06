package com.ppdai.framework.raptor.rpc;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public interface Response {

    Object getValue();

    Exception getException();

    String getRequestId();

    int getCode();

    Map<String, String> getAttachments();

    void setAttachment(String key, String value);

}
