package com.ppdai.framework.raptor.filter.refer;

import com.ppdai.framework.raptor.filter.AbstractFilter;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;

public class ReferMetricsFilter extends AbstractFilter implements ReferFilter{


    @Override
    public Response filter(Refer<?> refer, Request request) {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
