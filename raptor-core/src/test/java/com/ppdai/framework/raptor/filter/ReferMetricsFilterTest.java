package com.ppdai.framework.raptor.filter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.framework.raptor.filter.refer.ReferMetricsFilter;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.*;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReferMetricsFilterTest {

    @Test
    public void testFilter() {

        //TODO 构造真实的request、response
        Request request = new DefaultRequest();
        Response response = new DefaultResponse();

        Refer<?> refer = mock(Refer.class);
        when(refer.getServiceUrl()).thenReturn(URL.valueOf("http://localhost:8080/raptor/com.ppdai.test.Simple"));
        when(refer.call(request)).thenReturn(response);

        ReferMetricsFilter filter = new ReferMetricsFilter();
        filter.filter(refer, request);

        MetricRegistry metricRegistry = MetricContext.getMetricRegistry();
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.report();
    }
}
