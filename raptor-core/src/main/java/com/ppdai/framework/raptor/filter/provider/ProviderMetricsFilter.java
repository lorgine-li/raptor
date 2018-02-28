package com.ppdai.framework.raptor.filter.provider;

import com.ppdai.framework.raptor.filter.AbstractFilter;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.service.Provider;

public class ProviderMetricsFilter extends AbstractFilter implements ProviderFilter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Response filter(Provider<?> provider, Request request) {
        //TODO 记录metric
        return provider.call(request);
    }

}
