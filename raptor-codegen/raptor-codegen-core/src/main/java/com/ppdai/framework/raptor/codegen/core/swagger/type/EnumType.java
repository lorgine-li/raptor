package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Set;

/**
 * Created by zhangyicong on 18-2-28.
 */
public class EnumType implements Comparable<EnumType> {
    private String name;
    private String FQPN;
    private Set<String> values;

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

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }

    @Override
    public int compareTo(EnumType enumType) {
        return FQPN.compareTo(enumType.getFQPN());
    }
}
