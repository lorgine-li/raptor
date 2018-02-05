package com.ppdai.framework.raptor.serialization;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.serialize.ProtobufJsonSerialization;
import org.junit.Test;

public class ProtobufJsonSerializationTest {

    @Test
    public void test1() {
        Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        ProtobufJsonSerialization serialization = new ProtobufJsonSerialization();
        byte[] data = serialization.serializeMessage(request);

        System.out.println(new String(data));
        Helloworld.HelloRequest newRequest = serialization.deserializeMessage(data, Helloworld.HelloRequest.class);

        System.out.println(newRequest.getName());

    }
}
