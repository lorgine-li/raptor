package com.ppdai.framework.raptor.util;

import com.ppdai.framework.raptor.rpc.DefaultResponse;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;

public class RaptorFrameworkUtil {

    public static String getServiceKey(Request request) {
        return request.getInterfaceName();
    }

    public static String getAttachmentFromRequest(Request request, String key, String defaultValue) {
        String value = defaultValue;
        if (request.getAttachments() != null && request.getAttachments().containsKey(key)) {
            value = request.getAttachments().get(key);
        }
        return value;
    }

    /**
     * 输出请求的关键信息： requestId=** interface=** method=**
     *
     * @param request
     * @return
     */
    public static String toString(Request request) {
        return "requestId=" + request.getRequestId()
                + " interface=" + request.getInterfaceName()
                + " method=" + request.getMethodName();
    }

    public static Response buildErrorResponse(long requestId, Exception e) {
        DefaultResponse response = new DefaultResponse(requestId);
        response.setException(e);
        return response;
    }
}
