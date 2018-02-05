package com.ppdai.framework.raptor.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class ProtoBuffUtils {

    public static final int MAX_BODY_CHUNK_SIZE = 10_000_000;

    private ProtoBuffUtils() {

    }

    public static String convertProtoBuffToJson(Message protoObject) {
        try {
            return JsonFormat.printer().preservingProtoFieldNames().print(protoObject);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Convert Protocol Buffers object to JSON error.", e);
        }
    }

    public static String convertListToJson(List<? extends Message> messages) {
        if (messages == null) {
            return "";
        }
        List<String> jsonList = new ArrayList<>(messages.size());
        for (Message message : messages) {
            jsonList.add(convertProtoBuffToJson(message));
        }
        return new StringBuilder("[")
                .append(StringUtils.join(jsonList.toArray(),","))
                .append("]").toString();
    }

    public static String convertProtoBuffToJsonWithDefaultValues(Message protoObject) {
        try {
            return JsonFormat.printer().preservingProtoFieldNames().includingDefaultValueFields().print(protoObject);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Convert Protocol Buffers object to JSON error.", e);
        }
    }

    public static <TYPE extends Message> TYPE convertJsonToProtoBuff(JsonArray request, Class<TYPE> messageClass) {
        if (request == null || request.size() < 1 || request.get(0).isJsonNull()) {
            return null;
        }
        return convertJsonToProtoBuff(request.get(0).toString(), messageClass);
    }

    public static <TYPE extends Message> TYPE convertJsonToProtoBuff(String jsonString, Class<TYPE> messageClass) {
        if (jsonString == null) {
            return null;
        }
        if (!isValidJSON(jsonString)) {
            throw new RuntimeException("Input is not valid json. input = " + jsonString);
        }
        try {
            TYPE.Builder builder = getBuilder(messageClass);
            JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
            return (TYPE) builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing json to protobuf. Input = " + jsonString, e);
        }

    }

    private static <TYPE extends Message> TYPE.Builder getBuilder(Class<?> messageClass) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Constructor<?> constructor = messageClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        TYPE instance = (TYPE) constructor.newInstance();

        return instance.newBuilderForType();
    }

    private static boolean isValidJSON(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        if (!input.startsWith("{")) {
            return false;
        }
        try {
            new JsonParser().parse(input);
        } catch (JsonParseException ex) {
            return false;
        }
        return true;
    }

    public static <TYPE extends Message> TYPE newEmptyMessage(Class<TYPE> klass) {
        try {
            Message.Builder builder = getBuilder(klass);
            return (TYPE) builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing byte array to protobuf", e);
        }
    }

    public static <TYPE extends Message> TYPE byteArrayToProtobuf(byte data[], Class<TYPE> messageClass) {
        try {
            Message.Builder builder = getBuilder(messageClass);
            return (TYPE) builder.mergeFrom(data).build();
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing byte array to protobuf.", e);
        }
    }
}
