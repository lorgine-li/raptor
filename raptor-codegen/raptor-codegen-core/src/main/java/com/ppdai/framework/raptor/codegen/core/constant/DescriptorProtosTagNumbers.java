package com.ppdai.framework.raptor.codegen.core.constant;


/**
 *
 *  Created by zhangchengxi on 18-3-9.
 *
 *  google/protobuf/descriptor.proto 中的 tag number
 *  现在不全,用到可以补充
 */
public class DescriptorProtosTagNumbers {
    public static class FileDescriptorProto{
        public static final int NAME = 1;
        public static final int PACKAGE = 2;
        public static final int DEPENDENCY = 3;
        public static final int PUBLICDEPENDENCY = 10;
        public static final int WEAKDEPENDENCY = 11;
        public static final int MESSAGETYPE = 4;
        public static final int ENUMTYPE = 5;
        public static final int SERVICE = 6;
        public static final int EXTENSION = 7;
        public static final int OPTIONS = 8;
        public static final int SOURCECODEINFO = 9;
        public static final int SYNTAX = 12;
    }

    public static class ServiceDescriptorProto{
        public static final int NAME = 1;
        public static final int METHOD = 2;
        public static final int OPTIONS = 3;
    }

}
