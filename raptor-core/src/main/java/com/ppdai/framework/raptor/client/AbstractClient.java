package com.ppdai.framework.raptor.client;

import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.util.RaptorFrameworkUtil;
import com.ppdai.framework.raptor.exception.RaptorFrameworkException;

public abstract class AbstractClient<T> implements Client<T> {

    private Class<T> interfaceClass;

    protected URL serviceUrl;

    public AbstractClient(Class<T> interfaceClass, URL serviceUrl) {
        this.interfaceClass = interfaceClass;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Response call(Request request) {
        return doCall(request);
    }

    protected abstract Response doCall(Request request);

    @Override
    public URL getServiceUrl() {
        return this.serviceUrl;
    }

    @Override
    public Class<T> getInterface() {
        return this.interfaceClass;
    }
}
