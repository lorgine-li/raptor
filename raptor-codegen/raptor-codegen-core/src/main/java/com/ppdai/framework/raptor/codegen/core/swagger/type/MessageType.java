package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class MessageType extends AbstractType implements Comparable<MessageType>, Type {

    private String fullyQualifiedClassName;
    private List<FieldType> fields;


    public void setFields(List<FieldType> fields) {
        this.fields = fields;
    }

    public Collection<FieldType> getFieldTypeList() {
        return fields;
    }

    @Override
    public int compareTo(MessageType messageType) {
        return getFullyQualifiedPathName().compareTo((messageType.getFullyQualifiedPathName()));
    }

    @Override
    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    @Override
    public int hashCode() {
        return fullyQualifiedClassName.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        return anObject instanceof MessageType && fullyQualifiedClassName.equals(((MessageType) anObject).getFullyQualifiedClassName());
    }


}
