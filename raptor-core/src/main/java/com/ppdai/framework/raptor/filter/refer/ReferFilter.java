package com.ppdai.framework.raptor.filter.refer;

import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;

public interface ReferFilter {

    Response filter(Refer<?> refer, Request request);

    int getOrder();

}
