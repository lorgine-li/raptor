package com.ppdai.framework.raptor.codegen.core.swagger.type;


import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractType implements Type {
    private String name;
    private String fullyQualifiedPathName;
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
    public String getFullyQualifiedPathName() {
        return fullyQualifiedPathName;
    }

    @Override
    public void setFullyQualifiedPathName(String fullyQualifiedPathName) {
        this.fullyQualifiedPathName = fullyQualifiedPathName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFullyQualifiedClassName() {
        return StringUtils.join(new String[]{getPackageName(), getClassName(), getName()}, ProtobufConstant.PACKAGE_SEPARATOR);
    }
}
