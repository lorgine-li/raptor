package com.ppdai.framework.raptor.spring.converter;

import com.google.protobuf.Message;
import com.ppdai.framework.raptor.util.ProtoBuffUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.*;

public class ProtobufHttpMessageConverter extends org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter {

    private static final com.google.protobuf.util.JsonFormat.Printer JSON_PRINTER = com.google.protobuf.util.JsonFormat.printer().includingDefaultValueFields();


    @Override
    protected void writeInternal(Message message, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = getDefaultContentType(message);
        }
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            appendToOutputStream(message, outputMessage);
        } else {
            super.writeInternal(message, outputMessage);
        }
    }

    @Override
    protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            InputStream body = inputMessage.getBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = body.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String jsonString = result.toString("UTF-8");
            return ProtoBuffUtils.convertJsonToProtoBuff(jsonString,clazz);
        }else{
            return super.readInternal(clazz,inputMessage);
        }

    }


    private void appendToOutputStream(Message message, HttpOutputMessage outputMessage) throws IOException {
        PrintStream printStream = new PrintStream(outputMessage.getBody());
        JSON_PRINTER.appendTo(message, printStream);
    }

}
