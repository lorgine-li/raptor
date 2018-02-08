package com.ppdai.framework.raptor.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "raptor.client.apache")
public class ApacheHttpClientProperties {

    private int connectTimeout = -1;
    private int socketTimeout = -1;
    private int connectionRequestTimeout = -1;
    private int retryCount = 0;
    private boolean requestSentRetryEnabled = false;
    private int poolMaxTotal = 100;
    private int poolMaxPreRoute = 10;

}
