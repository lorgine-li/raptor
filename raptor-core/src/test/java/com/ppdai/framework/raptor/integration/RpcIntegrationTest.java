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
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class RpcIntegrationTest {

    private JettyServletEndpoint servletEndpoint;

    @Before
    public void setUp() throws Exception {
        // 生成servlet
        URL baseUrl = URL.builder()
                .port(8080)
                .path("/raptor")
                .build();
        servletEndpoint = new JettyServletEndpoint(baseUrl);
        servletEndpoint.start();
    }

    @After
    public void tearDown() throws Exception {
        servletEndpoint.stop();
    }

    @Test
    public void testServer() throws Exception {
        //初始化service
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = new DefaultProvider<>(Simple.class, simple);
        provider.init();
        servletEndpoint.export(provider);
    }

    @Test
    public void testRestCall() throws Exception {
        //启动服务端
        testServer();

        // 访求测试
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:8080/raptor/com.ppdai.framework.raptor.proto.Simple/sayHello");
        String req = "{\"name\":\"ppdai\"}";
        HttpEntity entity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(req).build();
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);

        String reply = EntityUtils.toString(response.getEntity());
        System.out.println(reply);
    }

    @Test
    public void testRpcCall() throws Exception {
        //启动服务端
        testServer();

        String url = "http://localhost:8080";
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
        apacheHttpClient.init();

        DefaultRefer<Simple> refer = new DefaultRefer<>(Simple.class, apacheHttpClient, URL.valueOf(url));
        ReferInvocationHandler invocationHandler = new ReferInvocationHandler(Simple.class, refer);

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
