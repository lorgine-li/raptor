package com.ppdai.framework.raptor.proto;

import org.apache.commons.lang3.RandomUtils;

public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        if ("exception".equalsIgnoreCase(request.getName())) {
            throw new RuntimeException("request error!");
        }
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt(0, 10000);
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
