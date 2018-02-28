package com.ppdai.framework.raptor.filter.refer;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.filter.AbstractMetricFilter;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;

public class ReferMetricsFilter extends AbstractMetricFilter implements ReferFilter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Response filter(Refer<?> refer, Request request) {
        URL serviceUrl = refer.getServiceUrl();
        return doFilter(serviceUrl, refer, request);
    }

    @Override
    protected String getNodeType() {
        return RaptorConstants.NODE_TYPE_REFER;
    }
}
