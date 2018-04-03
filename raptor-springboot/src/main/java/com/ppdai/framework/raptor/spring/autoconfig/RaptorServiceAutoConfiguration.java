package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.filter.provider.ProviderAccessLogFilter;
import com.ppdai.framework.raptor.filter.provider.ProviderFilter;
import com.ppdai.framework.raptor.filter.provider.ProviderMetricsFilter;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import com.ppdai.framework.raptor.service.ServletEndpoint;
import com.ppdai.framework.raptor.spring.converter.ProtobufHttpMessageConverter;
import com.ppdai.framework.raptor.spring.endpoint.RaptorProvidersActuatorEndpoint;
import com.ppdai.framework.raptor.spring.properties.ServletEndpointProperties;
import com.ppdai.framework.raptor.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Import({RaptorServiceProcessor.class})
@Configuration
@EnableConfigurationProperties({ServletEndpointProperties.class})
public class RaptorServiceAutoConfiguration {

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    @Autowired
    private Environment env;

    @Autowired
    private ServletEndpointProperties servletEndpointProperties;

    @Bean
    @ConditionalOnMissingBean(ServletEndpoint.class)
    public ServletEndpoint createServletEndpoint() {
        int port = Integer.parseInt(env.getProperty("server.port", "8080"));
        String basePath = servletEndpointProperties.getBasePath();
        URL baseUrl = URL.builder()
                .host(NetUtils.getLocalIp())
                .port(port)
                .path(basePath)
                .parameters(servletEndpointProperties.getParameters()).build();
        return new ServletEndpoint(baseUrl);
    }

    @Bean
    public ServletRegistrationBean registerServlet(ServletEndpoint servletEndpoint) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(servletEndpoint);
        Map<String, String> initParams = new HashMap<>();
        registrationBean.setInitParameters(initParams);
        List<String> urlMappings = new ArrayList<>();
        //先把开头的/删除，在加上/，保护下
        String path = RaptorConstants.PATH_SEPARATOR + StringUtils.removeStart(servletEndpoint.getBaseUrl().getPath(), RaptorConstants.PATH_SEPARATOR);
        path = StringUtils.removeEnd(path, RaptorConstants.PATH_SEPARATOR);
        urlMappings.add(path + "/*");
        registrationBean.setUrlMappings(urlMappings);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

    @Bean
    public ProviderBuilder createProviderBuilder(ObjectProvider<List<ProviderFilter>> providerFilters) {
        List<ProviderFilter> providerFilterList = providerFilters.getIfAvailable();
        return ProviderBuilder.newBuilder().addFilters(providerFilterList);
    }

    @Bean
    @ConditionalOnProperty(name = "raptor.provider.filter.accessLog", havingValue = "true", matchIfMissing = true)
    public ProviderAccessLogFilter createProviderAccessLogFilter() {
        return new ProviderAccessLogFilter();
    }

    @Bean
    @ConditionalOnProperty(name = "raptor.provider.filter.metrics", havingValue = "true", matchIfMissing = true)
    public ProviderMetricsFilter createProviderMetricFilter() {
        return new ProviderMetricsFilter();
    }

    @Configuration
    @ConditionalOnClass(AbstractEndpoint.class)
    static class EndpointConfig {

        @Bean
        @ConditionalOnClass(AbstractEndpoint.class)
        public RaptorProvidersActuatorEndpoint createRaptorServiceActuatorEndpoint(ServletEndpoint servletEndpoint) {
            return new RaptorProvidersActuatorEndpoint(servletEndpoint.getProviders());
        }
    }

}
