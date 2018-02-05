package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.URL;

import java.lang.reflect.Method;

public interface Provider<T> extends Caller {

    Method lookupMethod(String methodName, String parameterType);

    Class<T> getInterface();

    T getImpl();

    URL getServiceUrl();

    void setServiceUrl(URL serviceUrl);

    void destroy();

    void init();

}