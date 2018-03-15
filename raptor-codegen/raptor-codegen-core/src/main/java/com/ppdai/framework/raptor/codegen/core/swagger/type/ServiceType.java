package com.ppdai.framework.raptor.codegen.core.swagger.type;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class ServiceType {
    private String name;
    private String FQPN;
    private Map<String, MethodType> methods;
    private String packageName;

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

    public Map<String, MethodType> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, MethodType> methods) {
        this.methods = methods;
    }

    public Collection<MethodType> getMethodTypeList() {
        return methods.values();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }
}
