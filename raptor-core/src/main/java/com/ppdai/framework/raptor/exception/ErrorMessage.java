package com.ppdai.framework.raptor.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ErrorMessage implements Serializable, ExceptionAttachment {

    private int code;
    private String message;
    private Map<String, String> attachments;

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
        this.attachments = new HashMap<>();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ErrorMessage addAttachment(String name, String value) {
        this.attachments.put(name, value);
        return this;
    }


    public ErrorMessage addAttachments(Map<String, String> attachments) {
        if (attachments != null) {
            this.attachments.putAll(attachments);
        }
        return this;
    }

    public String getAttachment(String name) {
        return this.attachments.get(name);
    }

    @Override
    public Map<String, String> getAttachments() {
        return this.attachments;
    }

    public ErrorProto.ErrorMessage toErrorProto() {
        return ErrorProto.ErrorMessage.newBuilder()
                .setCode(this.code)
                .setMessage(this.message)
                .putAllAttachments(this.attachments)
                .build();
    }

    public static ErrorMessage fromErrorProto(ErrorProto.ErrorMessage errorProto) {
        ErrorMessage errorMessage = new ErrorMessage(errorProto.getCode(), errorProto.getMessage());
        if (errorProto.getAttachmentsMap() != null) {
            errorMessage.attachments = errorProto.getAttachmentsMap();
        }
        return errorMessage;
    }
}
