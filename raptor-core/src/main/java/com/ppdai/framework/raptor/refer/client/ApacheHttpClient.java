package com.ppdai.framework.raptor.refer.client;

import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

@Getter
@Setter
@Slf4j
public class ApacheHttpClient extends AbstractHttpClient {

    private int connectTimeout = 2000;
    private int socketTimeout = 10000;
    private int connectionRequestTimeout = -1;
    private int retryCount = 0;
    private boolean requestSentRetryEnabled = false;
    private int poolMaxTotal = 500;
    private int poolMaxPreRoute = 20;

    private CloseableHttpClient client;

    public ApacheHttpClient() {
    }
    public ApacheHttpClient(CloseableHttpClient client) {
        this.client = client;
    }

    @Override
    protected HttpResponse doSendRequest(HttpPost request, URL serviceUrl) throws IOException {
        request.setConfig(getRequestConfig(serviceUrl));
        CloseableHttpClient httpClient = this.getClient();
        return httpClient.execute(request);
    }

    @Override
    public void init() {
        super.init();
        if (this.client == null) {
            this.client = buildHttpClient();
        }
    }

    @Override
    public void destroy() {
        if (this.client != null) {
            try {
                client.close();
            } catch (IOException e) {
                log.error("error close client when destroy.", e);
            }
        }
    }

    protected RequestConfig getRequestConfig(URL serviceUrl) {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(serviceUrl.getIntParameter(URLParamType.connectTimeout.getName(), this.getConnectTimeout()))
                .setSocketTimeout(serviceUrl.getIntParameter(URLParamType.socketTimeout.getName(), this.getSocketTimeout()))
                .setConnectionRequestTimeout(serviceUrl.getIntParameter(URLParamType.connectionRequestTimeout.getName(), this.getConnectionRequestTimeout()));
        return builder.build();
    }

    protected CloseableHttpClient buildHttpClient() {
        RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(this.getRetryCount(), this.isRequestSentRetryEnabled());
        return HttpClients.custom()
                .disableContentCompression()
                .setConnectionManager(this.buildConnectionManager())
                .setDefaultRequestConfig(config)
                .setRetryHandler(retryHandler)
                .disableCookieManagement()
                .build();
    }

    protected HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(this.getPoolMaxTotal());
        connectionManager.setDefaultMaxPerRoute(this.getPoolMaxPreRoute());
        return connectionManager;
    }
}
