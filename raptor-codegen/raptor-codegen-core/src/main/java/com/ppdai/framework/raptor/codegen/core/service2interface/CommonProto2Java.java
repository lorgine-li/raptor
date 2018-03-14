package com.ppdai.framework.raptor.codegen.core.service2interface;

import com.google.common.collect.Maps;
import com.google.protobuf.DescriptorProtos.*;
import com.google.protobuf.ProtocolStringList;
import com.ppdai.framework.raptor.codegen.core.service2interface.printer.PrintServiceFile;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ppdai.framework.raptor.codegen.core.utils.CommonUtils.upperCamelCaseProcess;

public class CommonProto2Java {

    private static final Logger logger = LoggerFactory.getLogger(CommonProto2Java.class);

    private final String discoveryRoot;

    private final String generatePath;

    private final CommandProtoc commondProtoc;

    private Map<String, String> pojoTypes;

    private CommonProto2Java(String discoveryRoot, String generatePath, final File protocDependenciesPath) {
        this.discoveryRoot = discoveryRoot;
        this.generatePath = generatePath;
        this.commondProtoc =
                CommandProtoc.configProtoPath(discoveryRoot, protocDependenciesPath);
    }

    public static CommonProto2Java forConfig(String discoveryRoot, String generatePath,
                                             final File protocDependenciesPath) {
        return new CommonProto2Java(discoveryRoot, generatePath, protocDependenciesPath);
    }

    public void generateFile(String protoPath) {
        logger.info("    Processing : " + protoPath);
        if (pojoTypes == null) {
            pojoTypes = Maps.newHashMap();
        }

        if (!new File(protoPath).exists()) {
            logger.warn("protoPath:" + protoPath + " not exist, it may be in the third party jars, so it can't be generate.java");
            return;
        }
        FileDescriptorSet fileDescriptorSet = commondProtoc.invoke(protoPath);

        for (FileDescriptorProto fdp : fileDescriptorSet.getFileList()) {
            //No service has been defined.
            if (fdp.getServiceCount() == 0) {
                logger.info(fdp.getName() + " seems to has no Service defined.");
                continue;
            }

            Pair<String, String> packagePojoName = this.packagePojoName(fdp);
            logger.debug("Package name :" + packagePojoName.getLeft());
            logger.debug("POJO    name :" + packagePojoName.getRight());
            doGenerate(fdp, packagePojoName.getLeft(), packagePojoName.getRight());
        }

    }

    //todo option
    private Pair<String, String> packagePojoName(FileDescriptorProto fileDescriptorProto) {
        String packageName = null;
        String pojoClassName = null;
        /**
         * This `temp` field is used for the potential duplication file name for the POJOs and PrpcInterface generated from the same proto file.
         * */
        String tempPojoClassName = null;
        if (fileDescriptorProto.getPackage() == null) {
            logger.warn("Your package name is null which is not suggested.");
        }
        packageName = fileDescriptorProto.getPackage();

        tempPojoClassName = pojoNameProcess(fileDescriptorProto.getName());
        if(fileDescriptorProto.getServiceCount()==0){
            pojoClassName = tempPojoClassName;
        }else{
            //todo a proto file contains a couple of services.
            pojoClassName = tempPojoClassName.equals(fileDescriptorProto.getService(0).getName()) ? tempPojoClassName + "OuterClass" : tempPojoClassName;
        }
        return new ImmutablePair<String, String>(packageName, pojoClassName);

    }

    private String pojoNameProcess(String origin) {
        int begin = origin.contains("/") ? origin.indexOf("/") : 0;
        int end = origin.lastIndexOf('.');

        char[] cs = origin.substring(begin, end).toCharArray();
        return upperCamelCaseProcess(cs);
    }

    private String prpcInterfaceNameProcess(String origin) {
        char[] cs = origin.toCharArray();
        return upperCamelCaseProcess(cs);
    }



    private void doGenerate(FileDescriptorProto fdp, String javaPackage, String outerClassName) {
        ArrayList<DescriptorProto> messageDescList = new ArrayList(fdp.getMessageTypeList());
        ArrayList<ServiceDescriptorProto> serviceDescList = new ArrayList(fdp.getServiceList());
        ArrayList<EnumDescriptorProto> enumDescList = new ArrayList(fdp.getEnumTypeList());
        /**
         * Protocol allow to have enums inside the message
         * Without this stream , we may drop these enums.
         * */
        messageDescList.stream()
                .filter(temp -> temp.getEnumTypeList() != null)
                .forEach(temp -> enumDescList.addAll(temp.getEnumTypeList()));

        ProtocolStringList dependencyList = fdp.getDependencyList();
        for (String aDependencyList : dependencyList) {
            String dependencyPath = discoveryRoot + "/" + aDependencyList;
            importProcess(dependencyPath, messageDescList);
        }

        enumProcess(enumDescList, javaPackage, outerClassName);
        messageProcess(messageDescList, javaPackage, outerClassName);
        serviceProcess(serviceDescList, javaPackage, outerClassName, fdp.getName());
    }

    private void importProcess(String protoPath,ArrayList<DescriptorProto> messageDescList) {
        if (!new File(protoPath).exists()) {
            logger.warn("protoPath:" + protoPath + " not exist, it may be in the third party jars, so it can't be generate.java");
            return;
        }
        FileDescriptorSet fileDescriptorSet = commondProtoc.invoke(protoPath);
        if (!new File(protoPath).exists()) {
            logger.warn("protoPath:" + protoPath + " not exist, it may be in the third party jars, so it can't be generate.java");
            return;
        }
        for (FileDescriptorProto fdp : fileDescriptorSet.getFileList()) {

            Pair<String, String> importPackagePojoName = this.packagePojoName(fdp);

            ArrayList<DescriptorProto> temp = new ArrayList(fdp.getMessageTypeList());
            messageProcess(fdp.getMessageTypeList(),importPackagePojoName.getLeft(),importPackagePojoName.getRight());
        }
    }

    private void serviceProcess(List<ServiceDescriptorProto> serviceDescList, String javaPackage, String outerClassName, String protoFileName) {
        for (ServiceDescriptorProto serviceDesc : serviceDescList) {
            logger.info("    Generating Service : " + serviceDesc.getName());
            int dot = protoFileName.indexOf(".");
            String originPojoName = protoFileName.substring(0, dot);
            /**
             * For the case that the name of the RaptorInterface
             * is the same as the POJOs generated by protocol compiler.
             * */
            String tempRaptorInterfaceName = prpcInterfaceNameProcess(serviceDesc.getName());
            if (!tempRaptorInterfaceName.equals(originPojoName) && tempRaptorInterfaceName.equals(outerClassName)) {
                tempRaptorInterfaceName += "Service";
                logger.warn("Warn.Your RaptorInterface name should not be the same as the java src generated by proto compiler , so suffix:Service is used here. ");
            }

            PrintServiceFile serviceFile = new PrintServiceFile(generatePath, javaPackage, tempRaptorInterfaceName);
            try {
                serviceFile.setServiceMethods(serviceDesc.getMethodList());
                serviceFile.setPojoTypeCache(pojoTypes);
            } finally {
                serviceFile.print();
                logger.info("Finish generating the RaptorInterface : " + tempRaptorInterfaceName + "   <<<<<<<<<<<<<<<<<<<<<");
            }
        }
    }

    private void messageProcess(List<DescriptorProto> messageDescList, String javaPackage, String outerClassName) {
        for (DescriptorProto messageDesc : messageDescList) {
            String pojoClassType = messageDesc.getName();

            String pojoPackageName = javaPackage.toLowerCase() + "." + outerClassName;
            String fullpojoType = outerClassName + "." + pojoClassType;

            pojoTypes.put(pojoClassType, fullpojoType);

        }
    }

    private void enumProcess(List<EnumDescriptorProto> enumDescList, String javaPackage, String outerClassName) {
        for (EnumDescriptorProto enumDesc : enumDescList) {
            String enumClassType = enumDesc.getName();
            String enumPackageName = javaPackage + "." + outerClassName;
            String fullpojoType = enumPackageName.toLowerCase() + "." + enumClassType;
            pojoTypes.put(enumClassType, fullpojoType);

        }
    }

}
