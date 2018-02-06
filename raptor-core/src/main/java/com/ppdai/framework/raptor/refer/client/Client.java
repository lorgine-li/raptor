package com.ppdai.framework.raptor.refer.client;

import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;

public interface Client {
    void init();

    void destroy();

    Response sendRequest(Request request, URL serviceUrl);
}
