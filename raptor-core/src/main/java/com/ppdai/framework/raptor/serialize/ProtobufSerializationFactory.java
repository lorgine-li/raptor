package com.ppdai.framework.raptor.serialize;

import com.ppdai.framework.raptor.exception.RaptorFrameworkException;

public class ProtobufSerializationFactory implements SerializationFactory {
    private ProtobufBinSerialization binSerialization = new ProtobufBinSerialization();
    private ProtobufJsonSerialization jsonSerialization = new ProtobufJsonSerialization();

    @Override
    public Serialization newInstance(String name) {
        if (name != null && name.contains("json")) {
            return jsonSerialization;
        } else if (name != null && name.contains("protobuf")) {
            return binSerialization;
        }
        throw new RaptorFrameworkException("Can't find Serialization by name: " + name);
    }
}
