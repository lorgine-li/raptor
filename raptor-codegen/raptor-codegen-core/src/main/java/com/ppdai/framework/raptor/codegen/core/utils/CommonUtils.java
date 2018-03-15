package com.ppdai.framework.raptor.codegen.core.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;


public final class CommonUtils {

    public static String getPackageNameFromFQPN(String FQPN){
        // TODO: 2018/3/7 检查没有 package 的情况
        int lastDotIndex = FQPN.lastIndexOf('.');
        String result =  StringUtils.substring(FQPN, 0, lastDotIndex);
        lastDotIndex = result.lastIndexOf('.');
        if(lastDotIndex == -1 ) {
            return "";
        }
        if(CharUtils.isAsciiAlphaUpper(FQPN.charAt(lastDotIndex+1))){
            return getPackageNameFromFQPN(result);
        }else{
            return result;
        }

    }


    public static boolean isProtoBufType(String typeName) {
        return typeName.startsWith("google.protobuf");
    }
}
