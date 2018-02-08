package com.ppdai.framework.raptor.common;

import java.util.regex.Pattern;

public class RaptorConstants {

    public static final String HTTP = "http";
    public static final String DEFAULT_PATH_PREFIX = "raptor";
    public static final String METRIC_NAME = "raptor";
    public static final String SEPARATOR_ARRAY = ",";
    public static final String SEPARATOR_ACCESS_LOG = "|";
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    public static final String METHOD_CONFIG_PREFIX = "method.";
    public static final String HOST_PORT_SEPARATOR = ":";
    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PATH_SEPARATOR = "/";
    public static final String NODE_TYPE_SERVICE = "service";
    public static final String NODE_TYPE_REFER = "refer";
    public static final String REGISTRY_PROTOCOL_LOCAL = "local";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final String DEFAULT_CHARACTER = "utf-8";
    public static final int HTTP_EXPECTATION_FAILED = 419;
    public static final int HTTP_OK = 200;
    public static final int DEFAULT_PORT = 8080;



}
