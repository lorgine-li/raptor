package com.ppdai.framework.raptor.proto;

import com.ppdai.framework.raptor.annotation.RaptorInterface;

import javax.transaction.Transactional;

@Transactional
@RaptorInterface
public interface Simple {

    Helloworld.HelloReply sayHello(Helloworld.HelloRequest request);

}
