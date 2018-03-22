package com.ppdai.framework.raptor.rpc;


import com.ppdai.framework.raptor.common.ParamNameConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class RpcContextHelper {

    public static Map<String, String> getTraceRequestAttachments() {
        return getTraceAttachments(RpcContext.getContext().getRequestAttachments());
    }

    public static Map<String, String> getTraceResponseAttachments() {
        return getTraceAttachments(RpcContext.getContext().getResponseAttachments());
    }

    public static void traceRequest(Request request) {
        if (request != null) {
            Map<String, String> trace = getTraceAttachments(request.getAttachments());
            RpcContext.getContext().putAllRequestAttachments(trace);
        }
    }

    public static void traceResponse(Response response) {
        if (response != null) {
            Map<String, String> trace = getTraceAttachments(response.getAttachments());
            RpcContext.getContext().putAllResponseAttachments(trace);
        }
    }

    public static Map<String, String> getTraceAttachments(Map<String, String> attachments) {
        Map<String, String> traceAttachments = new HashMap<>();
        if (attachments != null) {
            for (Map.Entry<String, String> entry : attachments.entrySet()) {
                if (StringUtils.startsWith(entry.getKey(), ParamNameConstants.TRACE_HEADER_PREFIX)) {
                    traceAttachments.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return traceAttachments;
    }
}
