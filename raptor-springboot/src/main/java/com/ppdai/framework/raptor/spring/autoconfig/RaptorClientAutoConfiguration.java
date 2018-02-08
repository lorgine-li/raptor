package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.refer.ReferBuilder;
import com.ppdai.framework.raptor.refer.client.ApacheHttpClient;
import com.ppdai.framework.raptor.refer.client.Client;
import com.ppdai.framework.raptor.refer.repository.UrlRepository;
import com.ppdai.framework.raptor.spring.properties.ApacheHttpClientProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({ApacheHttpClientProperties.class})
@Import({RaptorClientPostProcessor.class})
public class RaptorClientAutoConfiguration implements EnvironmentAware {

    private Environment environment;

    private final static String DEFAULT_PREFIX = "raptor.url.";
    private final static String PREFIX_KEY = "raptor.url.prefix";

    @Autowired
    private ApacheHttpClientProperties apacheHttpClientProperties;

    @Bean
    @ConditionalOnProperty(name = "raptor.urlRepository", havingValue = "springEnv", matchIfMissing = true)
    public UrlRepository createUrlRepository() {
        SpringEnvUrlRepository springEnvUrlRepository = new SpringEnvUrlRepository(environment);
        springEnvUrlRepository.setKeyPrefix(environment.getProperty(PREFIX_KEY, DEFAULT_PREFIX));
        return springEnvUrlRepository;
    }

    @Bean
    @ConditionalOnMissingBean
    public Client createDefaultClient() {
        ApacheHttpClient client = new ApacheHttpClient();
        BeanUtils.copyProperties(apacheHttpClientProperties, client);
        client.init();
        return client;
    }

    @Bean
    public ReferBuilder createReferBuilder(Client client) {
        return new ReferBuilder(client);
    }

    @Bean
    public RaptorClientRegistry createClientRegistry(UrlRepository urlRepository, ReferBuilder referBuilder) {
        RaptorClientRegistry raptorClientRegistry = new RaptorClientRegistry(urlRepository, referBuilder);
        return raptorClientRegistry;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
