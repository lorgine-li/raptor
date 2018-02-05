package com.ppdai.framework.raptor.client;

import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.URL;

public interface Client<T> extends Caller {

    Class<T> getInterface();

    URL getServiceUrl();

    void init();

    void destroy();
}
