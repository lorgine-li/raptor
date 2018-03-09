package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.ppdai.framework.raptor.codegen.core.swagger.type.EnumType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;
import com.ppdai.framework.raptor.codegen.core.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhangyicong on 18-3-2.
 */
public class MetaContainer {
    private Map<String, EnumContainer> enumContainerMap = new HashMap<>();
    private Map<String, MessageContainer> messageContainerMap = new HashMap<>();

    public Map<String, EnumContainer> getEnumContainerMap() {
        return enumContainerMap;
    }

    public Map<String, MessageContainer> getMessageContainerMap() {
        return messageContainerMap;
    }

    public MessageType findMessageTypeByFQPN(String FQPN, String basePackage) {
        MessageContainer messageContainer = messageContainerMap.get(basePackage);
        MessageType message = messageContainer.findMessageTypeByFQPN(FQPN);
        return Optional.ofNullable(message).orElse(findMessageTypeByFQPN(FQPN));

    }

    public EnumType findEnumTypeByFQPN(String FQPN, String basePackage) {
        EnumContainer enumContainer = enumContainerMap.get(basePackage);
        EnumType enumType = enumContainer.findEnumTypeByFQPN(FQPN);
        return Optional.ofNullable(enumType).orElse(findEnumTypeByFQPN(FQPN));

    }

    private EnumType findEnumTypeByFQPN(String FQPN) {
        String packageName = CommonUtils.getPackageNameFromFQPN(FQPN);
        EnumContainer messageContainer = enumContainerMap.get(packageName);
        return Optional.ofNullable(messageContainer)
                .map(container -> container.findEnumTypeByFQPN(FQPN))
                .orElse(null);
    }

    private MessageType findMessageTypeByFQPN(String FQPN) {
        String packageName = CommonUtils.getPackageNameFromFQPN(FQPN);
        MessageContainer messageContainer = messageContainerMap.get(packageName);
        return Optional.ofNullable(messageContainer)
                .map(container -> container.findMessageTypeByFQPN(FQPN))
                .orElse(null);

    }
}
