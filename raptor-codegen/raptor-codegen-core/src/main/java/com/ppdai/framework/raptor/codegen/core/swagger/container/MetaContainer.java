package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.ppdai.framework.raptor.codegen.core.swagger.type.EnumType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;

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

    public MessageType findMessageTypeByFQPN(String FQPN) {
        MessageType message = messageContainer.findMessageTypeByFQPN(FQPN);
        return Optional.ofNullable(message).orElse(null);
    }

    public EnumType findEnumTypeByFQPN(String FQPN) {
        EnumType enumType = enumContainer.findEnumTypeByFQPN(FQPN);
        return Optional.ofNullable(enumType).orElse(null);
    }


}
