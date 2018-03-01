package com.ppdai.framework.raptor.spring.server;

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

/**
 * 测试只接入服务端
 */
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

}
