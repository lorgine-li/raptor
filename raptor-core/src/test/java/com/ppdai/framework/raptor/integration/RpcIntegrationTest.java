package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.proto.SimpleImpl;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

@Slf4j
public class RpcIntegrationTest extends RpcTestBase {

    @Test
    public void testServer() throws Exception {
        //初始化service
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = ProviderBuilder.newBuilder().build(Simple.class, simple);

        servletEndpoint.export(provider);
    }

    @Test
    public void testRestCall() throws Exception {
        //启动服务端
        testServer();

        // 请求测试
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

        Simple proxy = ReferProxyBuilder.newBuilder().build(Simple.class, URL.valueOf(url));

        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
        System.out.println(helloReply);
    }
}
