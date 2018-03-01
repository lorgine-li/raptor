package com.ppdai.framework.raptor.demo.server.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.ResultOuterClass;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import org.apache.commons.lang3.RandomUtils;

@RaptorService
public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String hello = "Hello " + request.getName() + " "
                + request.getSnippets(0) + " "
                + request.getCorpus() + " "
                + request.getCats(0) + " "
                + request.getResult()
                + ". " + RandomUtils.nextInt();

        return Helloworld.HelloReply.newBuilder().setMessage(hello)
                .addResults(ResultOuterClass.Result.newBuilder().setUrl("http://www.baidu.com").build()).build();
    }

}
