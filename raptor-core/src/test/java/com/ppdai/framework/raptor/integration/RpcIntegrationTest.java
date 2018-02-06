package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.refer.DefaultRefer;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.refer.client.ApacheHttpClient;
import com.ppdai.framework.raptor.refer.proxy.ReferInvocationHandler;
import com.ppdai.framework.raptor.refer.proxy.JdkProxyFactory;
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

        //TODO 访求测试
    }

    @Test
    public void testDirectCall() throws Exception {

        //启动服务端
        testServer();

        String url = "http://localhost:8080/raptor/" + Simple.class.getName();
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
        apacheHttpClient.init();

        DefaultRefer<Simple> refer = new DefaultRefer<>(Simple.class, apacheHttpClient, URL.valueOf(url));
        ReferInvocationHandler<Simple> invocationHandler = new ReferInvocationHandler<>(Simple.class, refer);

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
