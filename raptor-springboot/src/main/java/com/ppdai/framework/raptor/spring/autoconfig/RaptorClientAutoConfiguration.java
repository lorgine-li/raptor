package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.filter.refer.ReferAccessLogFilter;
import com.ppdai.framework.raptor.filter.refer.ReferFilter;
import com.ppdai.framework.raptor.filter.refer.ReferMetricsFilter;
import com.ppdai.framework.raptor.refer.ReferProxyBuilder;
import com.ppdai.framework.raptor.refer.client.ApacheHttpClient;
import com.ppdai.framework.raptor.refer.client.Client;
import com.ppdai.framework.raptor.refer.repository.AbstractUrlRepository;
import com.ppdai.framework.raptor.spring.endpoint.RaptorRefersActuatorEndpoint;
import com.ppdai.framework.raptor.spring.properties.ApacheHttpClientProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.List;

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
    public AbstractUrlRepository createUrlRepository() {
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
    public ReferProxyBuilder createReferProxyBuilder(Client client, ObjectProvider<List<ReferFilter>> referFilters) {
        List<ReferFilter> referFilterList = referFilters.getIfAvailable();
        return ReferProxyBuilder.newBuilder().addFilters(referFilterList).client(client);
    }

    @Bean
    public RaptorClientRegistry createClientRegistry(AbstractUrlRepository urlRepository, ReferProxyBuilder referProxyBuilder) {
        return new RaptorClientRegistry(urlRepository, referProxyBuilder);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConditionalOnProperty(name = "raptor.refer.filter.accessLog", havingValue = "true", matchIfMissing = true)
    public ReferAccessLogFilter createReferAccessLogFilter() {
        return new ReferAccessLogFilter();
    }

    @Bean
    @ConditionalOnProperty(name = "raptor.refer.filter.metrics", havingValue = "true", matchIfMissing = true)
    public ReferMetricsFilter createReferMetricFilter() {
        return new ReferMetricsFilter();
    }


    @Bean
    @ConditionalOnClass(AbstractEndpoint.class)
    public RaptorRefersActuatorEndpoint createRaptorReferActuatorEndpoint(RaptorClientRegistry raptorClientRegistry) {
        return new RaptorRefersActuatorEndpoint(raptorClientRegistry.getClientCache());
    }
}
