package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.filter.provider.ProviderFilter;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.util.NetUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProviderBuilder {

    private List<ProviderFilter> filters;

    private ProviderBuilder() {
        filters = new ArrayList<>();
    }

    public static ProviderBuilder newBuilder() {
        return new ProviderBuilder();
    }

    public <T> Provider<T> build(Class<T> interfaceClass, T serviceInstance) {
        URL serviceUrl = URL.builder()
                .host(NetUtils.getLocalIp())
                .path(URLParamType.basePath.getValue() + "/" + interfaceClass.getName())
                .build();
        return build(interfaceClass, serviceInstance, serviceUrl);
    }

    public <T> Provider<T> build(Class<T> interfaceClass, T serviceInstance, URL serviceUrl) {
        Provider<T> provider = new DefaultProvider<>(interfaceClass, serviceInstance);
        if (!CollectionUtils.isEmpty(filters)) {
            ProviderFilterDecorator providerFilterDecorator = new ProviderFilterDecorator(filters);
            provider = providerFilterDecorator.decorate(provider);
        }
        provider.setServiceUrl(serviceUrl);
        provider.init();
        return provider;
    }

    public ProviderBuilder addFilter(ProviderFilter filter) {
        if (Objects.nonNull(filter)) {
            this.filters.add(filter);
        }
        return this;
    }

    public ProviderBuilder addFilters(List<ProviderFilter> filters) {
        if (!CollectionUtils.isEmpty(filters)) {
            this.filters.addAll(filters);
        }
        return this;
    }
}
