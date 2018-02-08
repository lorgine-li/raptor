package com.ppdai.framework.raptor.demo.client;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootClient.class)
public class RaptorClientTest {

    @RaptorClient
    private Simple simple;

    @Test
    public void testClient() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply reply = simple.sayHello(helloRequest);
        System.out.println(reply);
        Assert.assertTrue(StringUtils.startsWith(reply.getMessage(), "Hello ppdai"));
    }
}
