package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import com.ppdai.framework.raptor.spring.aop.AopAnnotation1;
import com.ppdai.framework.raptor.spring.aop.AopAnnotation2;
import org.apache.commons.lang3.RandomUtils;

import javax.transaction.Transactional;

@RaptorService
public class SimpleImpl implements Simple {

    @AopAnnotation1
    @AopAnnotation2
    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
