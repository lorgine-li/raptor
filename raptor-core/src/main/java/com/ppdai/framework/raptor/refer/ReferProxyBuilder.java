package com.ppdai.framework.raptor.refer;

import com.ppdai.framework.raptor.filter.refer.ReferFilter;
import com.ppdai.framework.raptor.refer.client.ApacheHttpClient;
import com.ppdai.framework.raptor.refer.client.Client;
import com.ppdai.framework.raptor.refer.proxy.JdkProxyFactory;
import com.ppdai.framework.raptor.refer.proxy.ReferInvocationHandler;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.serialize.Serialization;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReferProxyBuilder {

    private Client client;

    private List<ReferFilter> filterList;

    private Serialization serialization;

    private ReferProxyBuilder() {
        this.filterList = new ArrayList<>();
    }

    public <T> T build(Class<T> interfaceClass, URL url) {
        if (this.client == null) {
            this.client = createDefaultClient();
        }
        if (this.serialization != null) {
            this.client.setSerialization(this.serialization);
        }
        Refer<T> refer = new DefaultRefer<>(interfaceClass, client, url);

        if (!CollectionUtils.isEmpty(filterList)) {
            ReferFilterDecorator decorator = new ReferFilterDecorator(filterList);
            refer = decorator.decorate(refer);
        }
        ReferInvocationHandler invocationHandler = new ReferInvocationHandler(interfaceClass, refer);

        return new JdkProxyFactory().getProxy(interfaceClass, invocationHandler);
    }

    private Client createDefaultClient() {
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
        apacheHttpClient.init();
        return apacheHttpClient;
    }

    public static ReferProxyBuilder newBuilder() {
        return new ReferProxyBuilder();
    }

    public ReferProxyBuilder addFilter(ReferFilter referFilter) {
        if (Objects.nonNull(referFilter)) {
            this.filterList.add(referFilter);
        }
        return this;
    }

    public ReferProxyBuilder addFilters(List<ReferFilter> referFilters) {
        if (!CollectionUtils.isEmpty(referFilters)) {
            this.filterList.addAll(referFilters);
        }
        return this;
    }

    public ReferProxyBuilder client(Client client) {
        this.client = client;
        return this;
    }

    public ReferProxyBuilder serialization(Serialization serialization) {
        this.serialization = serialization;
        return this;
    }
}
