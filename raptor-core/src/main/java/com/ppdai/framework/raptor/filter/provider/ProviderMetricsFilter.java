package com.ppdai.framework.raptor.filter.provider;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.filter.AbstractMetricFilter;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Provider;

public class ProviderMetricsFilter extends AbstractMetricFilter implements ProviderFilter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Response filter(Provider<?> provider, Request request) {
        URL serviceUrl = provider.getServiceUrl();
        return doFilter(serviceUrl, provider, request);
    }

    @Override
    protected String getNodeType() {
        return RaptorConstants.NODE_TYPE_SERVICE;
    }
}
