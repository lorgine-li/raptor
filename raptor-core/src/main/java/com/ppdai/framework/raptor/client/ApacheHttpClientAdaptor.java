package com.ppdai.framework.raptor.client;

import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import org.apache.http.client.HttpClient;

//TODO  使用apache httpClient发送请求
public class ApacheHttpClientAdaptor<T> extends AbstractClient<T> {

    private HttpClient client;

    public ApacheHttpClientAdaptor(Class<T> interfaceClass,  URL serviceUrl) {
        super(interfaceClass,  serviceUrl);
    }

    @Override
    protected Response doCall(Request request) {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
