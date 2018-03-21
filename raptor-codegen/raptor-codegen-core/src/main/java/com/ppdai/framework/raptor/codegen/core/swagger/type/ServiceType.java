package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Collection;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class ServiceType extends AbstractType {
    private String name;
    private String fullyQualifiedPathName;
    private Map<String, MethodType> methods;
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

    public Map<String, MethodType> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, MethodType> methods) {
        this.methods = methods;
    }

    public Collection<MethodType> getMethodTypeList() {
        return methods.values();
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
