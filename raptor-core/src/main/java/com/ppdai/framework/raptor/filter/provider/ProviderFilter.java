package com.ppdai.framework.raptor.filter.provider;

import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.service.Provider;

public interface ProviderFilter {

    Response filter(Provider<?> provider, Request request);

    int getOrder();
    
}
