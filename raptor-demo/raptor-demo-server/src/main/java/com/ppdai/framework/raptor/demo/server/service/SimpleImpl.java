package com.ppdai.framework.raptor.demo.server.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.ResultOuterClass;
import com.ppdai.framework.raptor.proto.Simple;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RaptorService
public class SimpleImpl implements Simple {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleImpl.class);

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String hello = "Hello " + "name: " + request.getName() + ", "
//                + "snippets: " + request.getSnippets(0) + ", "
                + "corpus: " + request.getCorpus() + ", "
//                + "cats: " + request.getCats(0) + ", "
                + "result: " + request.getResult() + ", "
                + "bool: " + request.getTbool() + ", "
                + "bytes: " + request.getTbytes().toStringUtf8() + ", "
                + "double: " + request.getTDouble() + ", "
                + "fixed32: " + request.getTfixed32() + ", "
                + "fixed64: " + request.getTfixed64() + ", "
                + "float: " + request.getTFloat() + ", "
                + "int32: " + request.getTint32() + ", "
                + "int64: " + request.getTint64() + ", "
                + "sfixed32: " + request.getTsfixed32() + ", "
                + "sfixed64: " + request.getTsfixed64() + ", "
                + "unit32: " + request.getTunit32() + ", "
                + "unit64: " + request.getTunit64() + ", "
                + "Timestamp: " + request.getTime()
                + "Struct: " + request.getStruct()
                + "Value: " + request.getValue()
                + "ListValue: " + request.getListValue()
                + "Duration: " + request.getDuration()
                + ". " + RandomUtils.nextInt();

        LOGGER.info("request: {}", hello);

        Helloworld.HelloReply build = Helloworld.HelloReply.newBuilder().setMessage(hello).setCode(0)
                .setCorpus(Helloworld.HelloRequest.Corpus.PRODUCTS)
                .addResults(ResultOuterClass.Result.newBuilder().setUrl("http://www.baidu.com").build()).build();
        return build;
    }

}
