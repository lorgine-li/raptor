package com.ppdai.framework.raptor.filter.provider;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.exception.RaptorBizException;
import com.ppdai.framework.raptor.filter.AbstractAccessLogFilter;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderAccessLogFilter extends AbstractAccessLogFilter implements ProviderFilter {

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public Response filter(Provider<?> provider, Request request) {
        URL serviceUrl = provider.getServiceUrl();
        boolean needLog = serviceUrl.getBooleanParameter(URLParamType.accessLog.getName(), URLParamType.accessLog.getBooleanValue());
        if (needLog) {
            long t = System.currentTimeMillis();
            Response response = null;

            try {
                response = provider.call(request);
                return response;
            } finally {
                long requestTime = System.currentTimeMillis() - t;
                logAccess(serviceUrl, request, response, requestTime);
            }
        }
        return provider.call(request);
    }

    @Override
    protected String getNodeType() {
        return RaptorConstants.NODE_TYPE_SERVICE;
    }

    //TODO 此处逻辑放到provider中
    @Override
    protected String getStatusCode(Response response) {
        Exception e = response.getException();
        if (e != null) {
            if (e instanceof RaptorBizException) {
                return String.valueOf(RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE);
            } else {
                return String.valueOf(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE);
            }
        }
        return String.valueOf(RaptorMessageConstant.SUCCESS);
    }
}
