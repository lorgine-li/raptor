package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Set;

/**
 * Created by zhangyicong on 18-2-28.
 */
public class EnumType extends AbstractType implements Comparable<EnumType>{
    private Set<String> values;
    private String fullyQualifiedClassName;


    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }

    @Override
    public int compareTo(EnumType enumType) {
        return getFullyQualifiedPathName().compareTo(enumType.getFullyQualifiedPathName());
    }


    @Override
    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    @Override
    public int hashCode(){
        return fullyQualifiedClassName.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        return anObject instanceof MessageType && fullyQualifiedClassName.equals(((MessageType) anObject).getFullyQualifiedClassName());
    }
}
