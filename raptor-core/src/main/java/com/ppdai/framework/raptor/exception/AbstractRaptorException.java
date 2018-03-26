package com.ppdai.framework.raptor.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public abstract class AbstractRaptorException extends RuntimeException implements ExceptionAttachment {

    private int code;
    private String message;
    private Map<String, String> attachments = new HashMap<>();

    public AbstractRaptorException(int code, String message, Map<String, String> attachments, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = code;
        if (attachments != null) {
            this.attachments = attachments;
        }
    }

    @Override
    public String getMessage() {
        String message = getOriginMessage();
        return "error_code: " + code + ", error_message: " + message;
    }

    public String getOriginMessage() {
        if (StringUtils.isBlank(super.getMessage())) {
            return message;
        }
        return super.getMessage();
    }

    public AbstractRaptorException putAttachment(String name, String value) {
        if (this.attachments != null) {
            this.attachments.put(name, value);
        }
        return this;
    }

}
