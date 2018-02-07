package com.ppdai.framework.raptor.refer;

import com.ppdai.framework.raptor.refer.client.Client;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.Getter;
import lombok.Setter;

public class ReferBuilder {

    //filters
    //

    @Getter
    @Setter
    private Client client;

    public ReferBuilder(Client client) {
        this.client = client;
    }

    public Refer<?> build(Class<?> interfaceClass, String uri) {
        URL serviceUrl = URL.valueOf(uri);
        return build(interfaceClass, serviceUrl);
    }

    public Refer<?> build(Class<?> interfaceClass, URL serviceUrl) {
        DefaultRefer<?> defaultRefer = new DefaultRefer<>(interfaceClass, client, serviceUrl);
        return decorateFilter(defaultRefer);
    }

    public Refer<?> decorateFilter(Refer<?> refer) {
        //TODO 使用过滤器装饰
        return refer;
    }
}
