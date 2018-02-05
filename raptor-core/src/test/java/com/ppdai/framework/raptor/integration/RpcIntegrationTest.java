package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.client.ApacheHttpClientAdaptor;
import com.ppdai.framework.raptor.client.proxy.DefaultInvocationHandler;
import com.ppdai.framework.raptor.client.proxy.JdkProxyFactory;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.proto.SimpleImpl;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.DefaultProvider;
import com.ppdai.framework.raptor.service.JettyServletEndpoint;
import com.ppdai.framework.raptor.service.Provider;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcIntegrationTest {

    @Test
    public void testServer() throws Exception {
        // 生成servlet
        URL baseUrl = URL.builder()
                .port(8080)
                .path("/raptor")
                .build();
        JettyServletEndpoint servletEndpoint = new JettyServletEndpoint(baseUrl);

        //初始化service
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = new DefaultProvider<>(Simple.class, simple);
        servletEndpoint.export(provider);

        TimeUnit.SECONDS.sleep(1000);
    }

    @Test
    public void testDirectCall() throws Exception {

        //启动服务端
        testServer();

        String url = "http://localhost:8080/raptor/" + Simple.class.getName();
        ApacheHttpClientAdaptor<Simple> clientAdaptor = new ApacheHttpClientAdaptor<>(Simple.class, URL.valueOf(url));
        DefaultInvocationHandler<Simple> invocationHandler = new DefaultInvocationHandler<>(Simple.class, clientAdaptor);

        Simple proxy = new JdkProxyFactory().getProxy(Simple.class, invocationHandler);
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        try {
            Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
            System.out.println(helloReply);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
