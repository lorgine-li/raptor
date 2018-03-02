package com.ppdai.framework.raptor.codegen.core.swagger.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;

/**
 * Created by zhangyicong on 18-3-1.
 */
public interface SwaggerTemplate {
    String applyTemplate(DescriptorProtos.FileDescriptorProto fdp,
                         MetaContainer metaContainer,
                         String apiVersion) throws JsonProcessingException;
}
