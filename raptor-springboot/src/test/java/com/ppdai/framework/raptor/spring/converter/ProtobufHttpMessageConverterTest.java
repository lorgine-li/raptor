package com.ppdai.framework.raptor.spring.converter;

import com.ppdai.framework.raptor.proto.Helloworld;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;

import javax.validation.constraints.AssertTrue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.ppdai.framework.raptor.proto.Test.TestProto;

public class ProtobufHttpMessageConverterTest {
    private static MediaType PROTOBUF = new MediaType("application", "x-protobuf");

    ;
    private static MediaType JSON = new MediaType("application", "json");
    private ProtobufHttpMessageConverter protobufHttpMessageConverter = new ProtobufHttpMessageConverter();

    @Test
    public void testWrite() throws Exception {
        TestProto testProto = TestProto.newBuilder().build();
        TestOutputMessage outputMessage = new TestOutputMessage();
        protobufHttpMessageConverter.writeInternal(testProto, outputMessage);
        String result = outputMessage.toString();
        Assert.assertTrue(result.startsWith("{"));
        Assert.assertTrue(result.endsWith("}"));
    }

    @Test
    public void testCamelCase() throws IOException {
        TestProto testProto = TestProto.newBuilder().setCamelCase("value").build();
        TestOutputMessage outputMessage = new TestOutputMessage();
        protobufHttpMessageConverter.writeInternal(testProto, outputMessage);
        String result = outputMessage.toString();
        Assert.assertTrue(result.contains("camelCase"));
    }

    @Test
    public void testDefaultValue() throws IOException {
        TestProto testProto = TestProto.newBuilder().setCamelCase("value").build();
        TestOutputMessage outputMessage = new TestOutputMessage();
        protobufHttpMessageConverter.writeInternal(testProto, outputMessage);
        String result = outputMessage.toString();
        Assert.assertTrue(result.contains("default"));
    }

    private class TestOutputMessage implements StreamingHttpOutputMessage {

        private OutputStream outputStream = new ByteArrayOutputStream();

        private HttpHeaders httpHeaders ;

        public TestOutputMessage(){
            httpHeaders = new HttpHeaders();

            httpHeaders.setContentType(JSON);
        }


        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return outputStream;
        }

        @Override
        public void setBody(StreamingHttpOutputMessage.Body body) {

        }

        @Override
        public String toString() {
            return outputStream.toString();
        }
    }

}