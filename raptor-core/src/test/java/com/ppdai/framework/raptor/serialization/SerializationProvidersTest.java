package com.ppdai.framework.raptor.serialization;

import com.ppdai.framework.raptor.serialize.ProtobufBinSerialization;
import com.ppdai.framework.raptor.serialize.ProtobufJsonSerialization;
import com.ppdai.framework.raptor.serialize.Serialization;
import com.ppdai.framework.raptor.serialize.SerializationProviders;
import org.junit.Assert;
import org.junit.Test;

public class SerializationProvidersTest {

    @Test
    public void testSpi() {
        Serialization serialization = SerializationProviders.getInstance().getSerialization(ProtobufBinSerialization.NAME);
        Assert.assertNotNull(serialization);
        Assert.assertTrue(serialization instanceof ProtobufBinSerialization);


        serialization = SerializationProviders.getInstance().getSerialization(ProtobufJsonSerialization.NAME);
        Assert.assertNotNull(serialization);
        Assert.assertTrue(serialization instanceof ProtobufJsonSerialization);
    }
}
