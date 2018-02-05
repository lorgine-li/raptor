package com.ppdai.framework.raptor.exception;

import java.io.Serializable;

public class RaptorMessage implements Serializable {

    private int status;
    private int code;
    private String message;

    public RaptorMessage(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public int getErrorCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
