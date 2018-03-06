package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.type.FieldType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * 存放从proto文件中提取的message类型
 */
public class MessageContainer {

    private String packageName;
    private Map<String, MessageType> messageTypeMap = new LinkedHashMap<>();

    public MessageContainer(String packageName) {
        this.packageName = packageName;
    }

    public void addMessageProto(String parent, DescriptorProtos.DescriptorProto descriptorProto) {
        MessageType messageType = new MessageType();
        messageType.setName((parent != null ? parent + "." : "") + descriptorProto.getName());
        messageType.setFQPN(packageName + "." + messageType.getName());

        Map<String, FieldType> fieldTypeMap = new LinkedHashMap<>();
        messageType.setFields(fieldTypeMap);

        for (DescriptorProtos.FieldDescriptorProto ffdp : descriptorProto.getFieldList()) {
            FieldType fieldType = new FieldType();
            fieldType.setName(ffdp.getName());
            fieldType.setType(ffdp.getType());
            fieldType.setLabel(ffdp.getLabel());
            fieldType.setTypeName(ffdp.getTypeName().replaceAll("^\\.", "")
                    .replace(packageName + ".", ""));
            fieldType.setMessage(messageType.getFQPN());
            fieldTypeMap.put(fieldType.getName(), fieldType);
        }

        messageTypeMap.put(messageType.getFQPN(), messageType);
    }

    public MessageType findMessageTypeByFQPN(String FQPN) {
        return messageTypeMap.get(FQPN);
    }

    public Collection<MessageType> getMessageTypeList() {
        return messageTypeMap.values();
    }
}
