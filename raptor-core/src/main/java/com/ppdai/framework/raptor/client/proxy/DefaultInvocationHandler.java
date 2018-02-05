package com.ppdai.framework.raptor.client.proxy;

import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.Request;

import java.lang.reflect.Method;

public class DefaultInvocationHandler<T> extends AbstractInvocationHandler<T> {
    private Caller caller;

    public DefaultInvocationHandler(Class<T> interfaceClass, Caller caller) {
        super(interfaceClass);
        this.caller = caller;
    }

    @Override
    public Object doInvoke(Request request) {
        return caller.call(request);
    }

    @Override
    protected Object invokeLocal(Method method, Object[] args) {
        //TODO
        return null;
    }
}
