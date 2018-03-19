package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Set;

/**
 * Created by zhangyicong on 18-2-28.
 */
public class EnumType extends AbstractType implements Comparable<EnumType>{
    private Set<String> values;
    private String FQCN;


    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }

    @Override
    public int compareTo(EnumType enumType) {
        return getFQPN().compareTo(enumType.getFQPN());
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
