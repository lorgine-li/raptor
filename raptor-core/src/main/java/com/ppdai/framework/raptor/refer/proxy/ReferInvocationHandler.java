package com.ppdai.framework.raptor.refer.proxy;

import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;

import java.lang.reflect.Method;

public class ReferInvocationHandler<T> extends AbstractInvocationHandler<T> {
    private Refer<T> refer;

    public ReferInvocationHandler(Class<T> interfaceClass, Refer<T> refer) {
        super(interfaceClass);
        this.refer = refer;
    }

    @Override
    public Object doInvoke(Request request) {
        Response response = refer.call(request);
        return response.getValue();
    }

    @Override
    protected Object invokeLocal(Method method, Object[] args) throws Exception {
        return method.invoke(refer, args);
    }
}
