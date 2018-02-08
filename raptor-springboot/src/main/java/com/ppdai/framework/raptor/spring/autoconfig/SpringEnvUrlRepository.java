package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.refer.repository.UrlRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.env.Environment;

public class SpringEnvUrlRepository extends UrlRepository {

    private Environment environment;

    @Setter
    @Getter
    private String keyPrefix = "";

    public SpringEnvUrlRepository(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getUrlString(String urlKey) {
        return environment.getProperty(this.keyPrefix + urlKey);
    }
}
