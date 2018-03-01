package com.ppdai.framework.raptor.demo.client;

import com.google.protobuf.ByteString;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;

@Component
@RestController
public class SayHelloController {

    @RaptorClient
    private Simple simple;

    @RequestMapping("/sayhello")
    public Object routeSayHello(@RequestParam("name") String name) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName(name)
                .setCorpus(Helloworld.HelloRequest.Corpus.UNIVERSAL)
                .addSnippets("snippets1")
                .addCats(Helloworld.Cat.newBuilder().setColor("black").build())
                .setResult(Helloworld.HelloRequest.Result.newBuilder().setCorpus(Helloworld.HelloRequest.Result.Corpus.IMAGES).build())
                .setTbytes(ByteString.copyFromUtf8("张轶丛"))
                .setTDouble(1.01)
                .setTFloat(1.02f)
                .setTfixed32(23)
                .setTfixed64(23232222332332323l)
                .setTint32(123)
                .setTint64(3232132555244324324l)
                .setTsfixed32(213)
                .setTsfixed64(323232323223232l)
                .setTunit32(123)
                .setTunit64(323213232133223l)
                .setTbool(true)
                .build();

        Helloworld.HelloReply helloReply = simple.sayHello(helloRequest);
        return helloReply != null ? helloReply.toString() : "null";
    }

}
