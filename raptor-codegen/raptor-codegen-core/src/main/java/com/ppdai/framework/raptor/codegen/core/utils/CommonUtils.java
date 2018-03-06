package com.ppdai.framework.raptor.codegen.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public final class CommonUtils {

    public static String getPackageNameFromFQPN(String FQPN){
        int lastDotIndex = FQPN.lastIndexOf('.');
        return StringUtils.substring(FQPN, 0, lastDotIndex);

    }

    public static String findPojoTypeFromCache(String sourceType, Map<String, String> pojoTypes) {
        String type = StringUtils.substring(sourceType, StringUtils.lastIndexOf(sourceType, ".") + 1);
        return pojoTypes.get(type);
    }

    public static String findNotIncludePackageType(String sourceType) {
        String type = StringUtils.substring(sourceType, StringUtils.lastIndexOf(sourceType, ".") + 1);
        return type;
    }

    /**
     * Make sure the irregular Strings become a standard  Upper-Camel-Case ones.
     * For example:
     *          "!hello world 3"  ->  "HelloWorld3"
     *          "import_test_b"   ->  "ImportTestB"
     * */
    public static String upperCamelCaseProcess(char[] cs) {
        boolean tag1 = true;//whether is the First Letter of a word or not
        boolean tag2 = true;//whether the letter before is among "a-z" "A-Z" "0-9" or not
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < cs.length; i++) {
            if ((cs[i] > 64 && cs[i] < 91) || (cs[i] > 47 && cs[i] < 58)) {
                result.append(cs[i]);
                tag1 = false;
                tag2 = true;
            } else if (cs[i] > 96 && cs[i] < 123) {
                if (!tag2 || tag1) {
                    result.append(cs[i] -= 32);
                    tag1 = false;
                    tag2 = true;
                } else {
                    result.append(cs[i]);
                }
            } else {
                tag1 = true;
                tag2 = false;
            }
        }
        return result.toString();
    }

}
