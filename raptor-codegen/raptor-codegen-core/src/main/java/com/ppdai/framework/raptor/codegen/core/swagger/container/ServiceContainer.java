package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MethodType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.ServiceType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * 存放从proto文件中提取的service类型
 */
public class ServiceContainer {

    private String packageName;
    private Map<String, ServiceType> serviceTypeMap = new LinkedHashMap<>();

    public ServiceContainer(String packageName) {
        this.packageName = packageName;
    }

    public void addServiceProto(DescriptorProtos.ServiceDescriptorProto sdp) {
        ServiceType serviceType = new ServiceType();
        serviceType.setName(sdp.getName());
        serviceType.setFQPN(packageName + "." + serviceType.getName());

        Map<String, MethodType> methodTypeMap = new LinkedHashMap<>();
        serviceType.setMethods(methodTypeMap);

        for (DescriptorProtos.MethodDescriptorProto mdp : sdp.getMethodList()) {
            MethodType methodType = new MethodType();
            methodType.setName(mdp.getName());
            methodType.setInputType(mdp.getInputType().replaceAll("^\\.", ""));
            methodType.setOutputType(mdp.getOutputType().replaceAll("^\\.", ""));
            methodTypeMap.put(methodType.getName(), methodType);
        }

        serviceTypeMap.put(serviceType.getName(), serviceType);
    }

    public ServiceType findServiceTypeByName(String name) {
        return serviceTypeMap.get(name);
    }

    public Collection<ServiceType> getServiceTypeList() {
        return serviceTypeMap.values();
    }

    public String getPackageName() {
        return packageName;
    }
}
