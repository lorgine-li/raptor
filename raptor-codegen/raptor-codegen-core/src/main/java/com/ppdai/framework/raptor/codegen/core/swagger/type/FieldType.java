package com.ppdai.framework.raptor.codegen.core.swagger.type;

import com.google.protobuf.DescriptorProtos;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class FieldType {
    private String name;
    private String typeName;
    private String message;
    private String FQPN;
    private DescriptorProtos.FieldDescriptorProto.Label label;
    private DescriptorProtos.FieldDescriptorProto.Type type;

    public String getFQPN() {
        return FQPN;
    }

    public void setFQPN(String FQPN) {
        this.FQPN = FQPN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DescriptorProtos.FieldDescriptorProto.Label getLabel() {
        return label;
    }

    public void setLabel(DescriptorProtos.FieldDescriptorProto.Label label) {
        this.label = label;
    }

    public DescriptorProtos.FieldDescriptorProto.Type getType() {
        return type;
    }

    public void setType(DescriptorProtos.FieldDescriptorProto.Type type) {
        this.type = type;
    }
}
