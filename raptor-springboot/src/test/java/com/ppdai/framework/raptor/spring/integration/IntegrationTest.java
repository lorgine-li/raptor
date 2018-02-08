package com.ppdai.framework.raptor.spring.integration;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import com.ppdai.framework.raptor.spring.server.ServerApplication;
import com.ppdai.framework.raptor.spring.server.SimpleImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@Import(SimpleImpl.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"raptor.url.com.ppdai.framework.raptor.proto.Simple=http://localhost:8080"})
public class IntegrationTest {

    @RaptorClient
    private Simple simple1;

    @Test
    public void testRpcCall() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply reply = simple1.sayHello(helloRequest);
        System.out.println(reply);
        Assert.assertTrue(StringUtils.startsWith(reply.getMessage(), "Hello ppdai"));
    }
}
