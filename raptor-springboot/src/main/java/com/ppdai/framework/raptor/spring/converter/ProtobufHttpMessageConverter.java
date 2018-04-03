package com.ppdai.framework.raptor.spring.converter;

import com.google.protobuf.Message;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.PrintStream;

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

    private void appendToOutputStream(Message message, HttpOutputMessage outputMessage) throws IOException {
        PrintStream printStream = new PrintStream(outputMessage.getBody());
        JSON_PRINTER.appendTo(message, printStream);
    }

}
