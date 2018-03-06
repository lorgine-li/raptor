package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;
import com.ppdai.framework.raptor.codegen.core.utils.CommonUtils;
import com.sun.istack.internal.NotNull;

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

    @NotNull
    public MessageType findMessageTypeByFQPN(String FQPN, String basePackage) {
        MessageContainer messageContainer = messageContainerMap.get(basePackage);
        MessageType message = messageContainer.findMessageTypeByFQPN(FQPN);
        return Optional.ofNullable(message).orElse(findMessageTypeByFQPN(FQPN));

    }

    private MessageType findMessageTypeByFQPN(String FQPN) {
        String packageName = CommonUtils.getPackageNameFromFQPN(FQPN);
        MessageContainer messageContainer = messageContainerMap.get(packageName);
        return messageContainer.findMessageTypeByFQPN(FQPN);
    }
}
