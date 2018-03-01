package com.ppdai.framework.raptor.filter;

import com.ppdai.framework.raptor.filter.provider.ProviderAccessLogFilter;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.service.Provider;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

public class ProviderAccessLogFilterTest {

    @Test
    public void testMockLog() {
        Logger logger = mock(Logger.class);
        logger.info("test log");

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(logger).info(argument.capture());

        System.out.println(argument.getValue());
    }

    @Test
    public void testFilter() {

        //TODO 构造真实的request、response
        Request request = new DefaultRequest();
        Response response = new DefaultResponse();

        Provider provider = mock(Provider.class);
        when(provider.getServiceUrl()).thenReturn(URL.valueOf("http://localhost:8080/raptor/com.ppdai.test.Simple"));
        when(provider.call(request)).thenReturn(response);

        Logger logger = mock(Logger.class);

        ProviderAccessLogFilter filter = new ProviderAccessLogFilter();
        filter.setLogger(logger);

        filter.filter(provider, request);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(logger).info(argument.capture());
        System.out.println(argument.getValue());
    }
}
