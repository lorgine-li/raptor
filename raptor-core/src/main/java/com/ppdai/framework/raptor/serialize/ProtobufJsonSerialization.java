package com.ppdai.framework.raptor.serialize;

import com.google.protobuf.Message;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.util.ProtoBuffUtils;

import java.io.UnsupportedEncodingException;

public class ProtobufJsonSerialization extends AbstractProtobufSerialization {

    public static final String NAME = "application/json";

    @Override
    public byte[] serializeMessage(Message message) {
        try {
            String jsonString = ProtoBuffUtils.convertProtoBuffToJson(message);
            return jsonString.getBytes(RaptorConstants.DEFAULT_CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding " + RaptorConstants.DEFAULT_CHARACTER, e);
        }
    }

    @Override
    public <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz) {
        try {
            return ProtoBuffUtils.convertJsonToProtoBuff(new String(bytes, RaptorConstants.DEFAULT_CHARACTER), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding " + RaptorConstants.DEFAULT_CHARACTER, e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
