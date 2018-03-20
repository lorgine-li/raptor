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

public class RpcExceptionTest extends RpcTestBase {

    @Test(expected = RaptorBizException.class)
    public void testRpc() {
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = ProviderBuilder.newBuilder().build(Simple.class, simple);
        servletEndpoint.export(provider);

        String url = "http://localhost:8080";

        Simple proxy = ReferProxyBuilder.newBuilder()
                .build(Simple.class, URL.valueOf(url));
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("exception").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
        System.out.println(helloReply);
    }

    @Test
    public void testRestCall() throws Exception {
        //启动服务端
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = ProviderBuilder.newBuilder().build(Simple.class, simple);
        servletEndpoint.export(provider);

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
        Assert.assertEquals(RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE, errorMessage.getCode());

    }

}
