package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.constant.DescriptorProtosTagNumbers;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MethodType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.ServiceType;
import org.apache.commons.collections4.ListUtils;

import java.util.*;

/**
 * Created by zhangyicong on 18-2-27.
 * 存放从proto文件中提取的service类型
 */
public class ServiceContainer {

    private Map<String, ServiceType> serviceTypeMap = new LinkedHashMap<>();

    public void addServiceProto(String packageName,DescriptorProtos.ServiceDescriptorProto sdp, List<DescriptorProtos.SourceCodeInfo.Location> locationList, final List<Integer> parentpath) {
        ServiceType serviceType = new ServiceType();
        serviceType.setName(sdp.getName());
        serviceType.setFQPN(packageName + "." + serviceType.getName());
        serviceType.setPackageName(packageName);

        Map<String, MethodType> methodTypeMap = new LinkedHashMap<>();
        serviceType.setMethods(methodTypeMap);

        int methodIndex = 0;
        for (DescriptorProtos.MethodDescriptorProto mdp : sdp.getMethodList()) {
            MethodType methodType = new MethodType();
            methodType.setName(mdp.getName());
            methodType.setInputType(mdp.getInputType().replaceAll("^\\.", ""));
            methodType.setOutputType(mdp.getOutputType().replaceAll("^\\.", ""));

            //处理 method 上的注释
            ArrayList<Integer> path = new ArrayList<>(parentpath);
            path.add(DescriptorProtosTagNumbers.ServiceDescriptorProto.METHOD);
            path.add(methodIndex++);

            Optional<DescriptorProtos.SourceCodeInfo.Location> currentPath
                    = locationList.stream()
                    .filter(location -> ListUtils.isEqualList(location.getPathList(), path))
                    .findAny();

            if (currentPath.isPresent()) {
                DescriptorProtos.SourceCodeInfo.Location currentLocation = currentPath.get();
                methodType.setLeadingComments(currentLocation.getLeadingComments());
                methodType.setTrailingComments(currentLocation.getTrailingComments());
            }
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
}
