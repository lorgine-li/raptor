package com.ppdai.framework.raptor.filter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.framework.raptor.filter.provider.ProviderMetricsFilter;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.service.Provider;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProviderMetricsFilterTest {

    @Test
    public void testFilter() {

        //TODO 构造真实的request、response
        Request request = new DefaultRequest();
        Response response = new DefaultResponse();

        Provider provider = mock(Provider.class);
        when(provider.getServiceUrl()).thenReturn(URL.valueOf("http://localhost:8080/raptor/com.ppdai.test.Simple"));
        when(provider.call(request)).thenReturn(response);


        ProviderMetricsFilter filter = new ProviderMetricsFilter();
        filter.filter(provider, request);

        MetricRegistry metricRegistry = MetricContext.getMetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.report();
    }
}
