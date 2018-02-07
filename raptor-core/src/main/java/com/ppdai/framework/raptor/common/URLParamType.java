package com.ppdai.framework.raptor.common;


import com.ppdai.framework.raptor.exception.RaptorServiceException;

public enum URLParamType {

    version("version", "1.0"),
    nodeType("nodeType", RaptorConstants.NODE_TYPE_SERVICE),
    transExceptionStack("transExceptionStack", true),
    retries("retries", 0),
    loadbalance("loadbalance", "roundrobin"),
    haStrategy("haStrategy", "failover"),
    serialization("Content-Type", "application/protobuf"),
    port("port", 0),
    httpSchema("httpSchema", "http"),
    httpVersion("statusLine.protocolVersion", "1.1"),
    httpStatusCode("statusLine.code", 0),
    httpReasonPhrase("statusLine.reasonPhrase", ""),
    accessLog("accessLog", true),
    clientHost("clientHost", ""),
    filter("filter", false),
    requestId("requestId", "0"),
    parameterTypes("parameterTypes", null),
    returnType("returnType", null),
    basePath("basePath", "/raptor"),
    appId("appId", ""),
    exceptionClassHeader("x-exception-class", RaptorServiceException.class.getName()),
    statusCode("code", 0),
    connectTimeout("connectTimeout", 2000),
    socketTimeout("socketTimeout", 10000),
    connectionRequestTimeout("connectionRequestTimeout", -1),
    //
    ;
    private String name;
    private String value;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    URLParamType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    URLParamType(String name, long longValue) {
        this.name = name;
        this.value = String.valueOf(longValue);
        this.longValue = longValue;
    }

    URLParamType(String name, int intValue) {
        this.name = name;
        this.value = String.valueOf(intValue);
        this.intValue = intValue;
    }

    URLParamType(String name, boolean boolValue) {
        this.name = name;
        this.value = String.valueOf(boolValue);
        this.boolValue = boolValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public boolean getBooleanValue() {
        return boolValue;
    }

}
