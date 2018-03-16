package com.ppdai.framework.raptor.codegen.core.swagger.type;


import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractType implements Type {
    private String name;
    private String FQPN;
    private String className;
    private String packageName;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFQPN() {
        return FQPN;
    }

    @Override
    public void setFQPN(String FQPN) {
        this.FQPN = FQPN;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFQCN() {
        return StringUtils.join(new String[]{getPackageName(), getClassName(), getName()}, ProtobufConstant.PACKAGE_SEPARATOR);
    }
}
