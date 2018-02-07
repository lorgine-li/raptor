package com.ppdai.framework.raptor.serialize;

import com.google.protobuf.Message;
import com.ppdai.framework.raptor.util.ProtoBuffUtils;

public class ProtobufBinSerialization extends ProtobufSerialization {

    @Override
    public byte[] serializeMessage(Message message) {
        return message.toByteArray();
    }

    @Override
    public <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz) {
        return ProtoBuffUtils.byteArrayToProtobuf(bytes, clazz);
    }

}
