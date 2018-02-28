package com.ppdai.framework.raptor.filter.refer;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.filter.AbstractAccessLogFilter;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReferAccessLogFilter extends AbstractAccessLogFilter implements ReferFilter {

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public Response filter(Refer<?> refer, Request request) {
        URL serviceUrl = refer.getServiceUrl();
        boolean needLog = serviceUrl.getBooleanParameter(URLParamType.accessLog.getName(), URLParamType.accessLog.getBooleanValue());
        if (needLog) {
            long t = System.currentTimeMillis();
            Response response = null;

            try {
                response = refer.call(request);
                return response;
            } finally {
                long requestTime = System.currentTimeMillis() - t;
                logAccess(serviceUrl, request, response, requestTime);
            }
        }
        return refer.call(request);
    }

    @Override
    protected String getNodeType() {
        return RaptorConstants.NODE_TYPE_SERVICE;
    }
}
