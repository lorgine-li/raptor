package com.ppdai.framework.raptor.spring.endpoint;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.Map;

public class RaptorRefersActuatorEndpoint extends AbstractEndpoint {
    private Map<String, Object> refers;

    public RaptorRefersActuatorEndpoint(Map<String, Object> refers) {
        super("RaptorRefers", false);
        this.refers = refers;
    }

    @Override
    public Object invoke() {
        return refers;
    }
}
