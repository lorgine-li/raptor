package com.ppdai.framework.raptor.filter.refer;

import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;

public class ReferMetricsFilter extends AbstractReferFilter {

    @Override
    public Response filter(Refer<?> refer, Request request) {
        return refer.call(request);
    }

}
