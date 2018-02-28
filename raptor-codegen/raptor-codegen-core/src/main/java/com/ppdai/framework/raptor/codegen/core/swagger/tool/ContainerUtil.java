package com.ppdai.framework.raptor.codegen.core.swagger.tool;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.EnumContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MessageContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-28.
 */
public class ContainerUtil {

    private static Map<String, EnumContainer> enumContainerMap = new HashMap<>();
    private static Map<String, MessageContainer> messageContainerMap = new HashMap<>();

    /**
     * 提取enum类型
     * @param fdp
     * @return
     */
    public static EnumContainer getEnumContainer(DescriptorProtos.FileDescriptorProto fdp) {
        EnumContainer enumContainer = enumContainerMap.get(fdp.getPackage());
        if (enumContainer == null) {
            enumContainer = new EnumContainer(fdp.getPackage());
            enumContainerMap.put(fdp.getPackage(), enumContainer);
        }

        for (DescriptorProtos.EnumDescriptorProto edp : fdp.getEnumTypeList()) {
            enumContainer.addEnumProto(null, edp);
        }

        addEnumProto(enumContainer, null, fdp.getMessageTypeList());

        return enumContainer;
    }

    /**
     * 递归提取enum
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
     * @param fdp
     * @return
     */
    public static MessageContainer getMessageContainer(DescriptorProtos.FileDescriptorProto fdp) {
        MessageContainer messageContainer = messageContainerMap.get(fdp.getPackage());
        if (messageContainer == null) {
            messageContainer = new MessageContainer(fdp.getPackage());
            messageContainerMap.put(fdp.getPackage(), messageContainer);
        }

        addMessageProto(messageContainer, null, fdp.getMessageTypeList());

        return messageContainer;
    }

    /**
     * 递归提取message
     * @param messageContainer
     * @param parent
     * @param dpList
     */
    private static void addMessageProto(MessageContainer messageContainer ,
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
     * @param fdp
     * @return
     */
    public static ServiceContainer getServiceContainer(DescriptorProtos.FileDescriptorProto fdp) {
        ServiceContainer serviceContainer = new ServiceContainer(fdp.getPackage());

        for (DescriptorProtos.ServiceDescriptorProto sdp : fdp.getServiceList()) {
            serviceContainer.addServiceProto(sdp);
        }

        return serviceContainer;
    }
}
