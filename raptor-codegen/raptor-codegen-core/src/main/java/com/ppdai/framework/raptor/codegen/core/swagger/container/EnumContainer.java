package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import com.ppdai.framework.raptor.codegen.core.swagger.type.EnumType;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;

/**
 * Created by zhangyicong on 18-2-28.
 * 存放从proto文件中提取的enum类型
 */
public class EnumContainer {

    private Map<String, EnumType> enumTypeMap = new LinkedHashMap<>();


    public void addEnumProto(String packageName,String className,String parent,
                             DescriptorProtos.EnumDescriptorProto descriptorProto) {

        EnumType enumType = new EnumType();
        enumType.setName((parent != null ? parent + "." : "") + descriptorProto.getName());
        enumType.setFQPN(packageName + "." + enumType.getName());
        enumType.setFQCN(StringUtils.join(new String[]{packageName, className, enumType.getName()}, ProtobufConstant.PACKAGE_SEPARATOR));
        enumType.setClassName(className);
        enumType.setPackageName(packageName);
        Set<String> values = new LinkedHashSet<>();
        enumType.setValues(values);

        for (DescriptorProtos.EnumValueDescriptorProto evdp : descriptorProto.getValueList()) {
            values.add(evdp.getName());
        }


        enumTypeMap.put(enumType.getFQPN(), enumType);
    }

    public Collection<EnumType> getEnumTypeList() {
        return enumTypeMap.values();
    }

    public EnumType findEnumTypeByFQPN(String FQPN) {
        return enumTypeMap.get(FQPN);
    }
}
