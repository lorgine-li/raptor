package com.ppdai.framework.raptor.codegen.core.swagger.type;

public interface Type {
    String getName();

    void setName(String name);

    String getFullyQualifiedPathName();

    void setFullyQualifiedPathName(String fullyQualifiedPathName);

    String getClassName();

    void setClassName(String className);

    String getPackageName();

    void setPackageName(String packageName);
}
