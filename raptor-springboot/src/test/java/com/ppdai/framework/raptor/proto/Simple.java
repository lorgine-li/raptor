package com.ppdai.framework.raptor.proto;

import com.ppdai.framework.raptor.annotation.RaptorInterface;

@RaptorInterface
public interface Simple {

    Helloworld.HelloReply sayHello(Helloworld.HelloRequest request);

}
