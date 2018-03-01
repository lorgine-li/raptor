package com.ppdai.framework.raptor.integration;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.framework.raptor.filter.provider.ProviderMetricsFilter;
import com.ppdai.framework.raptor.filter.refer.ReferMetricsFilter;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.proto.SimpleImpl;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import org.junit.Test;

public class RpcMetricsTest extends RpcTestBase {

    @Test
    public void testRpcCall() throws Exception {
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = ProviderBuilder.newBuilder()
                .addFilter(new ProviderMetricsFilter())
                .build(Simple.class, simple);
        servletEndpoint.export(provider);

        String url = "http://localhost:8080";
        Simple proxy = ReferProxyBuilder.newBuilder()
                .addFilter(new ReferMetricsFilter())
                .build(Simple.class, URL.valueOf(url));

        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
        System.out.println(helloReply);

        MetricRegistry metricRegistry = MetricContext.getMetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.report();
    }

}
