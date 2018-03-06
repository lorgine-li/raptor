package com.ppdai.framework.raptor.codegen.core.swagger.container;

import java.util.HashMap;
import java.util.Map;

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
}
