package com.ppdai.framework.raptor.demo.client;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class SayHelloController {

    @RaptorClient
    private Simple simple;

    @RequestMapping("/sayhello")
    public Object routeSayHello(@RequestParam("name") String name) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName(name).build();
        Helloworld.HelloReply helloReply = simple.sayHello(helloRequest);
        return helloReply != null ? helloReply.toString() : "null";
    }

}
