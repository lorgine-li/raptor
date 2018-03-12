package com.ppdai.framework.raptor.spring.server;

import com.ppdai.framework.raptor.proto.Helloworld;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class TestController {

    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    @RequestMapping(value = "/sayHelloBin", method = RequestMethod.POST)
    public Helloworld.HelloReply sayHelloBin(@RequestBody Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

    @RequestMapping(value = "/sayHelloJson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Helloworld.HelloReply sayHelloJson(@RequestBody Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
