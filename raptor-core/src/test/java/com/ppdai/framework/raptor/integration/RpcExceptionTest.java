package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.exception.ErrorProto;
import com.ppdai.framework.raptor.exception.RaptorBizException;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.proto.SimpleImpl;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import com.ppdai.framework.raptor.util.ProtoBuffUtils;
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

import java.util.Map;

public class RpcExceptionTest extends RpcTestBase {

    private void registryService() {
        Simple simple = new SimpleImpl() {
            @Override
            public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
                throw new RaptorBizException(30001, "service error.", null)
                        .putAttachment("myException", "myValue");
            }
        };
        Provider<Simple> provider = ProviderBuilder.newBuilder().build(Simple.class, simple);
        servletEndpoint.export(provider);

    }

    @Test
    public void testRpc() {
        registryService();

        String url = "http://localhost:8080";

        Simple proxy = ReferProxyBuilder.newBuilder()
                .build(Simple.class, URL.valueOf(url));
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        try {
            Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
            System.out.println(helloReply);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RaptorBizException);
            RaptorBizException bizException = ((RaptorBizException) e);
            Assert.assertEquals(30001, bizException.getCode());
            Assert.assertEquals("myValue", bizException.getAttachments().get("myException"));
        }
    }

    @Test
    public void testRestCall() throws Exception {
        registryService();

        // 请求测试
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:8080/raptor/com.ppdai.framework.raptor.proto.Simple/sayHello");
        String req = "{\"name\":\"exception\"}";
        HttpEntity entity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(req).build();
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);

        String reply = EntityUtils.toString(response.getEntity());
        System.out.println(reply);
        ErrorProto.ErrorMessage errorMessage = ProtoBuffUtils.convertJsonToProtoBuff(reply, ErrorProto.ErrorMessage.class);

        Assert.assertEquals(RaptorConstants.RAPTOR_ERROR, response.getStatusLine().getStatusCode());
        Assert.assertEquals(30001, errorMessage.getCode());

        Map<String, String> attachment = errorMessage.getAttachmentsMap();
        Assert.assertEquals("myValue", attachment.get("myException"));

    }

}
