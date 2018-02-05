package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.rpc.URL;

import java.util.Map;

public interface Endpoint {

    Map<String, Provider<?>> getProviders();

    URL export(Provider<?> provider, URL serviceUrl);

    URL export(Provider<?> provider);

}
