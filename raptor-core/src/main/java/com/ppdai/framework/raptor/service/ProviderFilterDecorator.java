package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.filter.provider.ProviderFilter;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProviderFilterDecorator {

    @Getter
    @Setter
    private List<ProviderFilter> providerFilters;

    public ProviderFilterDecorator(List<ProviderFilter> providerFilters) {
        this.providerFilters = providerFilters;
    }

    public <T> Provider<T> decorate(Provider<T> provider) {
        if (CollectionUtils.isEmpty(providerFilters)) {
            return provider;
        }
        sort();
        Provider<T> lastProvider = provider;
        for (ProviderFilter filter : providerFilters) {
            final ProviderFilter filterFinal = filter;
            final Provider<T> lastProviderFinal = lastProvider;
            lastProvider = new Provider<T>() {
                @Override
                public Response call(Request request) {
                    return filterFinal.filter(lastProviderFinal, request);
                }

                @Override
                public Method lookupMethod(String methodName, String parameterType) {
                    return provider.lookupMethod(methodName, parameterType);
                }

                @Override
                public Class<T> getInterface() {
                    return provider.getInterface();
                }

                @Override
                public T getImpl() {
                    return provider.getImpl();
                }

                @Override
                public URL getServiceUrl() {
                    return provider.getServiceUrl();
                }

                @Override
                public void setServiceUrl(URL serviceUrl) {
                    provider.setServiceUrl(serviceUrl);
                }

                @Override
                public void destroy() {
                    provider.destroy();
                }

                @Override
                public void init() {
                    provider.init();
                }
            };
        }
        return lastProvider;
    }

    private void sort() {
        if (!CollectionUtils.isEmpty(this.providerFilters)) {
            this.providerFilters.sort(new Comparator<ProviderFilter>() {
                @Override
                public int compare(ProviderFilter o1, ProviderFilter o2) {
                    return o1.getOrder() - o2.getOrder();
                }
            });
            Collections.reverse(this.providerFilters);
        }
    }
}
