package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.JettyServletEndpoint;
import org.junit.After;
import org.junit.Before;

public class RpcTestBase {

    protected JettyServletEndpoint servletEndpoint;

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

}
