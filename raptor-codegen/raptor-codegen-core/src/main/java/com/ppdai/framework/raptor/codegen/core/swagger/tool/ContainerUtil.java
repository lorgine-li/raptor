package com.ppdai.framework.raptor.codegen.core.swagger.tool;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.constant.DescriptorProtosTagNumbers;
import com.ppdai.framework.raptor.codegen.core.swagger.container.EnumContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MessageContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangyicong on 18-2-28.
 */
public class ContainerUtil {

    /**
     * 提取enum类型
     *
     * @param fdp
     * @return
     */
    public static EnumContainer getEnums(DescriptorProtos.FileDescriptorProto fdp,
                                         MetaContainer metaContainer) {

        EnumContainer enumContainer = metaContainer.getEnumContainerMap().get(fdp.getPackage());
        if (enumContainer == null) {
            enumContainer = new EnumContainer(fdp.getPackage());
            metaContainer.getEnumContainerMap().put(fdp.getPackage(), enumContainer);
        }

        for (DescriptorProtos.EnumDescriptorProto edp : fdp.getEnumTypeList()) {
            enumContainer.addEnumProto(null, edp);
        }

        addEnumProto(enumContainer, null, fdp.getMessageTypeList());

        return enumContainer;
    }

    /**
     * 递归提取enum
     *
     * @param enumContainer
     * @param parent
     * @param dpList
     */
    private static void addEnumProto(EnumContainer enumContainer,
                                     String parent,
                                     List<DescriptorProtos.DescriptorProto> dpList) {

        for (DescriptorProtos.DescriptorProto dp : dpList) {
            String newParent = (parent != null ? parent + "." : "") + dp.getName();

            for (DescriptorProtos.EnumDescriptorProto edp : dp.getEnumTypeList()) {
                enumContainer.addEnumProto(newParent, edp);
            }

            addEnumProto(enumContainer, newParent, dp.getNestedTypeList());
        }
    }

    /**
     * 提取message类型
     *
     * @param fdp
     * @return
     */
    public static MessageContainer getMessages(DescriptorProtos.FileDescriptorProto fdp,
                                               MetaContainer metaContainer) {

        MessageContainer messageContainer = metaContainer.getMessageContainerMap().get(fdp.getPackage());
        if (messageContainer == null) {
            messageContainer = new MessageContainer(fdp.getPackage());
            metaContainer.getMessageContainerMap().put(fdp.getPackage(), messageContainer);
        }

        addMessageProto(messageContainer, null, fdp.getMessageTypeList());

        return messageContainer;
    }

    /**
     * 递归提取message
     *
     * @param messageContainer
     * @param parent
     * @param dpList
     */
    private static void addMessageProto(MessageContainer messageContainer,
                                        String parent,
                                        List<DescriptorProtos.DescriptorProto> dpList) {

        for (DescriptorProtos.DescriptorProto dp : dpList) {
            messageContainer.addMessageProto(parent, dp);
            addMessageProto(messageContainer, (parent != null ? parent + "." : "") + dp.getName(),
                    dp.getNestedTypeList());
        }
    }

    /**
     * 提取service类型
     *
     * @param fdp
     * @return
     */
    public static ServiceContainer getServiceContainer(DescriptorProtos.FileDescriptorProto fdp) {
        ServiceContainer serviceContainer = new ServiceContainer(fdp.getPackage());

        List<DescriptorProtos.SourceCodeInfo.Location> locationList = fdp.getSourceCodeInfo().getLocationList();

        int serviceIndex = 0;
        for (DescriptorProtos.ServiceDescriptorProto sdp : fdp.getServiceList()) {
            List<Integer> currentPath
                    = Arrays.asList(DescriptorProtosTagNumbers.FileDescriptorProto.SERVICE, serviceIndex++);

            serviceContainer.addServiceProto(sdp, locationList, currentPath);
        }

        return serviceContainer;
    }
}
