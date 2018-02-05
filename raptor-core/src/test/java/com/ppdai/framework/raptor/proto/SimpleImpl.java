package com.ppdai.framework.raptor.proto;

import org.apache.commons.lang3.RandomUtils;

public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt(0, 10000);
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
