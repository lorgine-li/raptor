package com.ppdai.framework.raptor.codegen.core.service2interface;

import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.codegen.core.CodegenConfiguration;
import com.ppdai.framework.raptor.codegen.core.service2interface.printer.InterfacePrinter;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.ContainerUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.type.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Created by zhongyi on 2018/1/4.
 */
public class InterfaceGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(InterfaceGenerator.class);

    private File outputDirectory;
    private File[] inputDirectories;
    private File protocDependenciesPath;

    public InterfaceGenerator codegenConfigure(CodegenConfiguration codegenConfiguration) {
        if (Objects.nonNull(codegenConfiguration)) {
            this.outputDirectory = codegenConfiguration.getOutputDirectory();
            this.inputDirectories = codegenConfiguration.getInputDirectories();
            this.protocDependenciesPath = codegenConfiguration.getProtocDependenciesPath();
        } else {
            throw new NullPointerException("CodegenConfiguration is null");
        }
        return this;
    }

    public void generate() throws Exception {
        performService2RaptorInterface();
    }

    private void performService2RaptorInterface() {
        LOGGER.info(">>>>>>>>>>>>>>>  Started performing SERVICEs -> INTERFACEs   ");
        //收集所有proto中message 和enum信息
        MetaContainer metaContainer = ContainerUtil.getMetaContainer(inputDirectories, protocDependenciesPath);

        ServiceContainer serviceContainer = metaContainer.getServiceContainer();
        for (ServiceType serviceType : serviceContainer.getServiceTypeList()) {
            InterfacePrinter interfacePrinter = new InterfacePrinter(outputDirectory.getAbsolutePath(), serviceType.getPackageName(), serviceType.getName());
            interfacePrinter.setServiceType(serviceType);
            interfacePrinter.setMetaContainer(metaContainer);
            interfacePrinter.print();
        }

    }

}
