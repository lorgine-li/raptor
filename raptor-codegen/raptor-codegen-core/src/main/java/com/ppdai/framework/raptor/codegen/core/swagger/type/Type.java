package com.ppdai.framework.raptor.codegen.core.swagger.type;

public interface Type {
    String getName();

    void setName(String name);

    String getFQPN();

    void setFQPN(String FQPN);

    String getClassName();

    void setClassName(String className);

    String getPackageName();

    void setPackageName(String packageName);
}
