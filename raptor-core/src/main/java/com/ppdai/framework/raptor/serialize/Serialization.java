package com.ppdai.framework.raptor.serialize;

import java.io.IOException;

public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

    String getName();
}
