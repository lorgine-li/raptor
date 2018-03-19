package com.ppdai.framework.raptor.codegen.core.swagger.type;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class FieldType extends AbstractType{
    private String message;
    private DescriptorProtos.FieldDescriptorProto.Label label;
    private DescriptorProtos.FieldDescriptorProto.Type type;
    private String typeName;


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

    @Override
    public String getFQCN() {
        return StringUtils.join(new String[]{getPackageName(), getClassName(), getTypeName()}, ProtobufConstant.PACKAGE_SEPARATOR);
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
