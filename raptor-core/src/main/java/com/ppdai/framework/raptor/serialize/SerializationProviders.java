package com.ppdai.framework.raptor.serialize;

import com.ppdai.framework.raptor.exception.RaptorFrameworkException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SerializationProviders {

    private static SerializationProviders providers;

    public static SerializationProviders getInstance() {
        if (providers != null) {
            return providers;
        }
        synchronized (SerializationProviders.class) {
            if (providers != null) {
                return providers;
            }
            providers = new SerializationProviders();
        }
        return providers;
    }

    private Map<String, Serialization> registry = new ConcurrentHashMap<>();

    private SerializationProviders() {
        ServiceLoader<Serialization> loaders = ServiceLoader.load(Serialization.class);
        for (Serialization serialization : loaders) {
            Serialization registered = registry.get(serialization.getName());
            if (registered != null) {
                log.warn("Serialization name: {} had already registered by class: {}", serialization.getName(), registered.getClass());
                continue;
            }
            registry.put(serialization.getName(), serialization);
        }
    }

    public Serialization getDefault() {
        return providers.getSerialization(ProtobufBinSerialization.name);
    }

    public Serialization getSerialization(String name) {
        return registry.get(name);
    }

    public void register(Serialization serialization) {
        Serialization registered = registry.get(serialization.getName());
        if (registered != null) {
            throw new RaptorFrameworkException(String.format("Serialization name: %s had already registered by class: %s", serialization.getName(), registered.getClass()));
        }
        registry.put(serialization.getName(), serialization);
    }

}
