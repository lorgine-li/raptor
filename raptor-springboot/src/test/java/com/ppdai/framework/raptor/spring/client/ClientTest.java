package com.ppdai.framework.raptor.spring.client;

import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.TestApplication;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试只接入客户端
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class,
        properties = {"raptor.url.com.ppdai.framework.raptor.proto.Simple=http://localhost:8080",
                "simple2=http://test1.ppdai.com"})
public class ClientTest {

    @RaptorClient
    private Simple simple1;

    @RaptorClient(url = "${simple2}")
    private Simple simple2;

    @Test
    public void testInit() {
        Assert.assertNotNull(simple1);
        Assert.assertNotNull(simple2);

        Assert.assertNotEquals(simple1, simple2);
    }
}
