package com.ppdai.framework.raptor.spring.converter;

import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.googlecode.protobuf.format.HtmlFormat;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.ProtobufFormatter;
import com.googlecode.protobuf.format.XmlFormat;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;

public class ProtobufHttpMessageConverter extends org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter {


    private static final ProtobufFormatter XML_FORMAT = new XmlFormat();
    private static final ProtobufFormatter HTML_FORMAT = new HtmlFormat();
    private static final com.google.protobuf.util.JsonFormat.Printer JSON_PRINTER = com.google.protobuf.util.JsonFormat.printer().includingDefaultValueFields();


    @Override
    protected void writeInternal(Message message, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = getDefaultContentType(message);
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(), charset);
            TextFormat.print(message, outputStreamWriter);
            outputStreamWriter.flush();
        } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            appendToOutputStream(message, outputMessage);
        } else if (MediaType.APPLICATION_XML.isCompatibleWith(contentType)) {
            XML_FORMAT.print(message, outputMessage.getBody(), charset);
        } else if (MediaType.TEXT_HTML.isCompatibleWith(contentType)) {
            HTML_FORMAT.print(message, outputMessage.getBody(), charset);
        } else if (PROTOBUF.isCompatibleWith(contentType)) {
            setProtoHeader(outputMessage, message);
            FileCopyUtils.copy(message.toByteArray(), outputMessage.getBody());
        }
    }

    private void appendToOutputStream(Message message, HttpOutputMessage outputMessage) throws IOException {
        PrintStream printStream = new PrintStream(outputMessage.getBody());
        JSON_PRINTER.appendTo(message, printStream);
    }

    /**
     * Set the "X-Protobuf-*" HTTP headers when responding with a message of
     * content type "application/x-protobuf"
     * <p><b>Note:</b> <code>outputMessage.getBody()</code> should not have been called
     * before because it writes HTTP headers (making them read only).</p>
     */
    private void setProtoHeader(HttpOutputMessage response, Message message) {
        response.getHeaders().set(X_PROTOBUF_SCHEMA_HEADER, message.getDescriptorForType().getFile().getName());
        response.getHeaders().set(X_PROTOBUF_MESSAGE_HEADER, message.getDescriptorForType().getFullName());
    }

}
