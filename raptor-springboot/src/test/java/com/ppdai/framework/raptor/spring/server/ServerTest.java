package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.spring.TestApplication;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 测试只接入服务端
 */
@RestController
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ServerTest {

    @Test
    public void testServer() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:8080/raptor/com.ppdai.framework.raptor.proto.Simple/sayHello");
        String req = "{\"name\":\"ppdai\"}";
        HttpEntity entity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(req).build();
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);

        String reply = EntityUtils.toString(response.getEntity());

        System.out.println(reply);
        Assert.assertTrue(StringUtils.contains(reply, "Hello ppdai"));
    }


    @Test
    public void testSayHelloJson() throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:8080/sayHelloJson");
        String req = "{\"name\":\"ppdai\"}";
        HttpEntity entity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(req).build();
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);

        String reply = EntityUtils.toString(response.getEntity());

        System.out.println(reply);
        Assert.assertTrue(StringUtils.contains(reply, "Hello ppdai"));
    }

    @Test
    public void testSayHelloBin() throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:8080/sayHelloBin");
        Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        HttpEntity entity = EntityBuilder.create()
                .setContentType(ContentType.create("application/x-protobuf"))
                .setBinary(request.toByteArray())
                .build();
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);

        byte[] content = EntityUtils.toByteArray(response.getEntity());
        Helloworld.HelloReply reply = Helloworld.HelloReply.parseFrom(content);
        System.out.println(reply);
        Assert.assertTrue(StringUtils.contains(reply.getMessage(), "Hello ppdai"));
    }

}
