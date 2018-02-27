package com.ppdai.framework.raptor.codegen.core.swagger.type;

import com.google.protobuf.DescriptorProtos;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class FieldType {
    private String name;
    private DescriptorProtos.FieldDescriptorProto.Type type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DescriptorProtos.FieldDescriptorProto.Type getType() {
        return type;
    }

    public void setType(DescriptorProtos.FieldDescriptorProto.Type type) {
        this.type = type;
    }
}
