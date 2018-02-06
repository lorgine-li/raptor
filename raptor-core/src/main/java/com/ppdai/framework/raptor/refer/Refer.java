package com.ppdai.framework.raptor.refer;

import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.URL;

public interface Refer<T> extends Caller {

    Class<T> getInterface();

    URL getServiceUrl();

}
