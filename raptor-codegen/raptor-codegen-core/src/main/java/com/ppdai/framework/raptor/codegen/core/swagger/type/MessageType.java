package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class MessageType extends AbstractType implements Comparable<MessageType>, Type {

    private String FQCN;
    private Map<String, FieldType> fields;

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
        return getFQPN().compareTo((messageType.getFQPN()));
    }

    @Override
    public String getFQCN() {
        return FQCN;
    }

    public void setFQCN(String FQCN) {
        this.FQCN = FQCN;
    }

    @Override
    public int hashCode(){
        return FQCN.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        return anObject instanceof MessageType && FQCN.equals(((MessageType) anObject).getFQCN());
    }


}
