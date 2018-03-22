package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.RpcContext;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.JettyServletEndpoint;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class CustomHeaderTest {

    private Map<String, String> request1Attachment = new HashMap<>();
    private Map<String, String> request2Attachment = new HashMap<>();
    private Map<String, String> response1Attachment = new HashMap<>();
    private Map<String, String> response2Attachment = new HashMap<>();

    private Map<String, RpcContext> contextMap = new HashMap<>();

    private String requestHeader = "requestHeader";
    private String requestTrace = ParamNameConstants.TRACE_HEADER_PREFIX + "requestTrace";

    private String responseHeader = "responseHeader";
    private String responseTrace = ParamNameConstants.TRACE_HEADER_PREFIX + "responseTrace";

    private String context1 = "context1";
    private String context2 = "context2";

    /**
     * 测试用户传递header
     * 启动两个jettyServer，分别注册服务Service1和Service2
     * client1调用Service1服务，Service1服务调用Service2服务
     *
     * client1请求中传递普通header和trace header，验证Service2中能获取到trace header
     *
     * Service2响应中传递普通header和trace header，验证client1中能获取到trace header
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        startServer1();
        startServer2();

        callService1();

        //request1Context中只包含requestTrace
        Assert.assertFalse(contextMap.get(context1).getRequestAttachments().containsKey(requestHeader));
        Assert.assertTrue(contextMap.get(context1).getRequestAttachments().containsKey(requestTrace));
        //request2Context中只包含requestTrace
        Assert.assertFalse(contextMap.get(context2).getRequestAttachments().containsKey(requestHeader));
        Assert.assertTrue(contextMap.get(context2).getRequestAttachments().containsKey(requestTrace));


        //request1Attachment中包含requestHeader/requestTrace
        Assert.assertTrue(request1Attachment.containsKey(requestHeader));
        Assert.assertTrue(request1Attachment.containsKey(requestTrace));
        //request2Attachment中只包含requestTrace
        Assert.assertFalse(request2Attachment.containsKey(requestHeader));
        Assert.assertTrue(request2Attachment.containsKey(requestTrace));


        //response1Attachment中只包含responseTrace
        Assert.assertFalse(response1Attachment.containsKey(responseHeader));
        Assert.assertTrue(response1Attachment.containsKey(responseTrace));
        //response2Attachment中包含responseHeader/responseTrace
        Assert.assertTrue(response2Attachment.containsKey(responseHeader));
        Assert.assertTrue(response2Attachment.containsKey(responseTrace));

    }

    private void startServer1() throws Exception {
        JettyServletEndpoint endpoint = startServer(8081);
        Provider<Service1> provider = ProviderBuilder.newBuilder().build(Service1.class, new Service1() {
            @Override
            public Helloworld.HelloReply service(Helloworld.HelloRequest request) {
                request1Attachment.putAll(RpcContext.getContext().getRequest().getAttachments());
                contextMap.put(context1, RpcContext.getContext());

                callService2();

                return Helloworld.HelloReply.newBuilder().setMessage(request.getName()).build();
            }
        });
        endpoint.export(provider);
    }

    private void startServer2() throws Exception {
        JettyServletEndpoint endpoint = startServer(8082);
        Provider<Service2> provider = ProviderBuilder.newBuilder().build(Service2.class, new Service2() {
            @Override
            public Helloworld.HelloReply service(Helloworld.HelloRequest request) {
                request2Attachment.putAll(RpcContext.getContext().getRequest().getAttachments());
                contextMap.put(context2, RpcContext.getContext());

                RpcContext.getContext().putResponseAttachment(responseHeader, responseHeader);
                RpcContext.getContext().putResponseAttachment(responseTrace, responseTrace);

                return Helloworld.HelloReply.newBuilder().setMessage(request.getName()).build();
            }
        });
        endpoint.export(provider);
    }

    private void callService1() {
        RpcContext.getContext().putRequestAttachment(requestHeader, requestHeader);
        RpcContext.getContext().putRequestAttachment(requestTrace, requestTrace);

        String url = "http://localhost:8081";
        Service1 proxy = ReferProxyBuilder.newBuilder().build(Service1.class, URL.valueOf(url));
        proxy.service(Helloworld.HelloRequest.newBuilder().setName("ppdai").build());

        response1Attachment.putAll(RpcContext.getContext().getResponse().getAttachments());
    }

    public void callService2() {

        String url = "http://localhost:8082";
        Service2 proxy = ReferProxyBuilder.newBuilder().build(Service2.class, URL.valueOf(url));
        proxy.service(Helloworld.HelloRequest.newBuilder().setName("ppdai").build());
        response2Attachment.putAll(RpcContext.getContext().getResponse().getAttachments());
    }

    public JettyServletEndpoint startServer(int port) throws Exception {
        // 生成servlet
        URL baseUrl = URL.builder()
                .port(port)
                .path("/raptor")
                .build();
        JettyServletEndpoint servletEndpoint = new JettyServletEndpoint(baseUrl);
        servletEndpoint.start();
        return servletEndpoint;
    }

    public interface Service1 {
        Helloworld.HelloReply service(Helloworld.HelloRequest request);
    }

    public interface Service2 {
        Helloworld.HelloReply service(Helloworld.HelloRequest request);
    }
}
