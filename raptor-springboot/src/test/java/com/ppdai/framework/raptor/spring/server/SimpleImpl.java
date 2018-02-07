package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import org.apache.commons.lang3.RandomUtils;

@RaptorService
public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
