package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.ppdai.framework.raptor.codegen.core.swagger.type.EnumType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.Type;

import java.util.Optional;

/**
 * Created by zhangyicong on 18-3-2.
 */
public class MetaContainer {
    private EnumContainer enumContainer = new EnumContainer();
    private MessageContainer messageContainer = new MessageContainer();
    private ServiceContainer serviceContainer = new ServiceContainer();

    public ServiceContainer getServiceContainer() {
        return serviceContainer;
    }

    public EnumContainer getEnumContainer() {
        return enumContainer;
    }

    public MessageContainer getMessageContainer() {
        return messageContainer;
    }

    public MessageType findMessageTypeByFullyQualifiedPathName(String fullyQualifiedPathName) {
        MessageType message = messageContainer.findMessageTypeByFullyQualifiedPathName(fullyQualifiedPathName);
        return Optional.ofNullable(message).orElse(null);
    }

    public EnumType findEnumTypeByFullyQualifiedPathName(String fullyQualifiedPathName) {
        EnumType enumType = enumContainer.findEnumTypeByFullyQualifiedPathName(fullyQualifiedPathName);
        return Optional.ofNullable(enumType).orElse(null);
    }

    public Type findTypeByFullyQualifiedPathName(String fullyQualifiedPathName) {
        Type messageTypeByFullyQualifiedPathName = findMessageTypeByFullyQualifiedPathName(fullyQualifiedPathName);
        return Optional.ofNullable(messageTypeByFullyQualifiedPathName)
                .orElse(findEnumTypeByFullyQualifiedPathName(fullyQualifiedPathName));
    }
}
