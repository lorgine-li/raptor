package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class MessageType implements Comparable<MessageType> {
    private String name;
    private String FQPN;
    private Map<String, FieldType> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFQPN() {
        return FQPN;
    }

    public void setFQPN(String FQPN) {
        this.FQPN = FQPN;
    }

    public Map<String, FieldType> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldType> fields) {
        this.fields = fields;
    }

    public Collection<FieldType> getFieldTypeList() {
        return fields.values();
    }

    @Override
    public int compareTo(MessageType messageType) {
        return FQPN.compareTo((messageType.getFQPN()));
    }
}
