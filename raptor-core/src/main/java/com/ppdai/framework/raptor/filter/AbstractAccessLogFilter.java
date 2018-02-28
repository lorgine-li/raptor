package com.ppdai.framework.raptor.filter;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;

public abstract class AbstractAccessLogFilter extends AbstractFilter {

    protected abstract String getNodeType();

    protected void logAccess(URL serviceUrl, Request request, Response response, long requestTime) {
        StringBuilder builder = new StringBuilder(128);
        //side 客户端/服务端
        append(builder, getNodeType());
        //client host
        append(builder, getClientHost(request));
        //server host
        append(builder, getServerHost(response));
        //interface
        append(builder, request.getInterfaceName());
        //method
        append(builder, request.getMethodName());
        //version
        append(builder, getInterfaceVersion(serviceUrl));
        //raptor Version
        append(builder, getRaptorVersion());
        //appId
        append(builder, getAppId());
        //requestId
        append(builder, request.getRequestId());
        //request payload size
        append(builder, getRequestPayloadSize(request));
        //response payload size
        append(builder, getResponsePayloadSize(response));
        //status code
        append(builder, getStatusCode(response));
        //requestTime
        append(builder, String.valueOf(requestTime));
    }

    protected void append(StringBuilder builder, Object field) {
        if (field != null) {
            builder.append(field.toString());
        }
        builder.append(RaptorConstants.SEPARATOR_ACCESS_LOG);
    }
}
