package com.ppdai.framework.raptor.filter;

import com.ppdai.framework.raptor.common.RaptorInfo;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.ppdai.framework.raptor.common.ParamNameConstants.*;

@Slf4j
public class AbstractFilter {

    protected String getClientHost(Request request) {
        //TODO 设置clientHost到request中
        return request.getAttachments().get(CLIENT_HOST);
    }

    protected String getServerHost(Response response) {
        //TODO 设置serverHost到request中
        return response.getAttachments().get(SERVER_HOST);
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

    protected Long getRequestPayloadSize(Request request) {
        //TODO 设置requestPayload到request中
        String requestPayloadSizeStr = request.getAttachments().get(REQUEST_PAYLOAD_SIZE);
        if (StringUtils.isNotBlank(requestPayloadSizeStr)) {
            try {
                return Long.parseLong(requestPayloadSizeStr);
            } catch (Exception e) {
                log.warn("Parse long value '{}' error,", requestPayloadSizeStr, e);
            }
        }
        return null;
    }

    protected Long getResponsePayloadSize(Response response) {
        //TODO 设置responsePayload到response中
        String responsePayloadSizeStr = response.getAttachments().get(RESPONSE_PAYLOAD_SIZE);
        if (StringUtils.isNotBlank(responsePayloadSizeStr)) {
            try {
                return Long.parseLong(responsePayloadSizeStr);
            } catch (Exception e) {
                log.warn("Parse long value '{}' error,", responsePayloadSizeStr, e);
            }
        }
        return null;
    }

    protected String getStatusCode(Response response) {
        if (response == null) {
            return String.valueOf(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE);
        }
        return String.valueOf(response.getCode());
    }

}
