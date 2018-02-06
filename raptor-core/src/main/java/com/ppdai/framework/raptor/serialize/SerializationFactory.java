package com.ppdai.framework.raptor.serialize;

public interface SerializationFactory {

    Serialization newInstance(String name);

}
