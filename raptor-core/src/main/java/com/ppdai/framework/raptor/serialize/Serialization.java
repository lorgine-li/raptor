package com.ppdai.framework.raptor.serialize;

public interface Serialization {

    String getName();

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
