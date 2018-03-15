package com.ppdai.framework.raptor.spring.endpoint;


import com.ppdai.framework.raptor.service.Provider;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.Map;

public class RaptorProvidersActuatorEndpoint extends AbstractEndpoint {

    private Map<String, Provider<?>> providers;

    public RaptorProvidersActuatorEndpoint(Map<String, Provider<?>> providers) {
        super("RaptorProviders",false);
        this.providers = providers;
    }

    @Override
    public Object invoke() {
        return providers;
    }
}
