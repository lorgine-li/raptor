package com.ppdai.framework.raptor.integration;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.proto.SimpleImpl;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.serialize.ProtobufJsonSerialization;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import org.junit.Test;

public class RpcSerializationTest extends RpcTestBase {

    @Test
    public void name() {

        Simple simple = new SimpleImpl();
        Provider<Simple> provider = ProviderBuilder.newBuilder().build(Simple.class, simple);
        servletEndpoint.export(provider);


        String url = "http://localhost:8080";

        Simple proxy = ReferProxyBuilder.newBuilder()
                .serialization(new ProtobufJsonSerialization())
                .build(Simple.class, URL.valueOf(url));
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
    }
}
