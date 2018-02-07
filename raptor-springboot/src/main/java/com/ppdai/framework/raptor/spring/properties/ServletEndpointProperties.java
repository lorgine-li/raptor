package com.ppdai.framework.raptor.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@Data
@ConfigurationProperties(prefix = "raptor.servletEndpoint")
public class ServletEndpointProperties {

    private String basePath = "raptor";

    private HashMap<String, String> parameters = new HashMap<>();
}
