package com.ppdai.framework.raptor.filter;

import com.ppdai.framework.raptor.filter.provider.ProviderAccessLogFilter;
import com.ppdai.framework.raptor.filter.refer.ReferAccessLogFilter;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.service.Provider;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReferAccessLogFilterTest {

    @Test
    public void testFilter() {

        //TODO 构造真实的request、response
        Request request = new DefaultRequest();
        Response response = new DefaultResponse();


        Refer<?> refer = mock(Refer.class);
        when(refer.getServiceUrl()).thenReturn(URL.valueOf("http://localhost:8080/raptor/com.ppdai.test.Simple"));
        when(refer.call(request)).thenReturn(response);

        Logger logger = mock(Logger.class);

        ReferAccessLogFilter filter = new ReferAccessLogFilter();
        filter.setLogger(logger);

        filter.filter(refer, request);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(logger).info(argument.capture());
        System.out.println(argument.getValue());
    }
}
