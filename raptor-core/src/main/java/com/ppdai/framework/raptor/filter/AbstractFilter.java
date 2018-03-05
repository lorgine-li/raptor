package com.ppdai.framework.raptor.filter;

import com.ppdai.framework.raptor.common.RaptorInfo;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.extern.slf4j.Slf4j;

import static com.ppdai.framework.raptor.common.ParamNameConstants.HOST_CLIENT;
import static com.ppdai.framework.raptor.common.ParamNameConstants.HOST_SERVER;

@Slf4j
public class AbstractFilter {

    protected String getClientHost(Request request) {
        return request.getAttachments().get(HOST_CLIENT);
    }

    protected String getServerHost(Response response) {
        return response.getAttachments().get(HOST_SERVER);
    }

    protected String getInterfaceVersion(URL serviceUrl) {
        return serviceUrl.getVersion();
    }

    protected String getAppId() {
        return RaptorInfo.getInstance().getAppId();
    }

    protected String getRaptorVersion() {
        return RaptorInfo.getInstance().getVersion();
    }

    protected String getStatusCode(Response response) {
        if (response == null) {
            return String.valueOf(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE);
        }
        return String.valueOf(response.getCode());
    }

}
