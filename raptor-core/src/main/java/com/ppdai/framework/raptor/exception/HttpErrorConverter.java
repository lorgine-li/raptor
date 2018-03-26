package com.ppdai.framework.raptor.exception;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;

import java.util.Map;

public class HttpErrorConverter {

    public static ErrorProto.ErrorMessage getErrorMessage(Throwable e) {
        if (e == null) {
            return ErrorProto.ErrorMessage.newBuilder().setCode(RaptorMessageConstant.SUCCESS).setMessage("SUCCESS").build();
        }
        if (e instanceof RaptorBizException) {
            AbstractRaptorException exception = (RaptorBizException) e;
            int code = exception.getCode() < RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE ? RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE : exception.getCode();
            return ErrorProto.ErrorMessage.newBuilder()
                    .setCode(code)
                    .setMessage(exception.getOriginMessage())
                    .putAllAttachments(exception.getAttachments())
                    .build();
        } else if (e instanceof AbstractRaptorException) {
            AbstractRaptorException exception = (AbstractRaptorException) e;
            return ErrorProto.ErrorMessage.newBuilder()
                    .setCode(exception.getCode())
                    .setMessage(exception.getOriginMessage())
                    .putAllAttachments(exception.getAttachments())
                    .build();
        }

        ErrorProto.ErrorMessage errorMessage = ErrorProto.ErrorMessage.newBuilder()
                .setCode(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE)
                .setMessage(e.getMessage()).build();

        if (e instanceof ExceptionAttachment) {
            Map<String, String> attachments = ((ExceptionAttachment) e).getAttachments();
            errorMessage.getAttachmentsMap().putAll(attachments);
        }
        return errorMessage;
    }

    public static int getHttpStatusCode(Throwable e) {
        if (e == null) {
            return RaptorConstants.HTTP_OK;
        }
        if (e instanceof AbstractRaptorException) {
            int code = ((AbstractRaptorException) e).getCode();
            return getHttpStatusCode(code);
        }
        return getDefaultErrorHttpStatusCode();
    }

    public static int getHttpStatusCode(int raptorCode) {
        if (raptorCode == RaptorMessageConstant.SUCCESS) {
            return RaptorConstants.HTTP_OK;
        }
        return getDefaultErrorHttpStatusCode();
    }

    public static int getDefaultErrorHttpStatusCode() {
        //定义raptor异常的status code
        return RaptorConstants.RAPTOR_ERROR;
    }
}
