package com.ppdai.framework.raptor.rpc;

import com.ppdai.framework.raptor.exception.RaptorServiceException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultResponse implements Response, Serializable {
    private static final long serialVersionUID = 4281186647291615871L;

    private int code = -1;
    private Object value;
    private Throwable exception;
    private String requestId;

    private Map<String, String> attachments = new HashMap<>();

    public DefaultResponse() {
    }

    public DefaultResponse(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public Object getValue() {
        if (exception != null) {
            throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new RaptorServiceException(exception.getMessage(), exception);
        }
        return value;
    }

    @Override
    public void setAttachment(String key, String value) {
        if (this.attachments == null) {
            this.attachments = new HashMap<>();
        }
        this.attachments.put(key, value);
    }

}
