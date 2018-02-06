package com.ppdai.framework.raptor.refer;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.refer.client.Client;
import com.ppdai.framework.raptor.rpc.URL;
import org.apache.commons.lang3.StringUtils;

public class ReferBuilder<T> {

    //filters
    //

    public Refer<T> buildWithDefaultPath(Class<T> interfaceClass, Client client, String uri) {
        return build(interfaceClass, client, uri, null);
    }

    public Refer<T> build(Class<T> interfaceClass, Client client, String uri, String path) {
        URL serviceUrl = URL.valueOf(uri);
        String servicePath = StringUtils.removeEnd(serviceUrl.getPath(), RaptorConstants.PATH_SEPARATOR);
        if (StringUtils.isBlank(path)) {
            path = RaptorConstants.PATH_SEPARATOR + RaptorConstants.DEFAULT_PATH_PREFIX + RaptorConstants.PATH_SEPARATOR + interfaceClass.getName();
        } else {
            path = RaptorConstants.PATH_SEPARATOR + StringUtils.removeFirst(path, RaptorConstants.PATH_SEPARATOR);
        }
        serviceUrl.setPath(servicePath + path);
        return build(interfaceClass, client, serviceUrl);
    }

    public Refer<T> build(Class<T> interfaceClass, Client client, URL serviceUrl) {
        DefaultRefer<T> defaultRefer = new DefaultRefer<>(interfaceClass, client, serviceUrl);
        return decorateFilter(defaultRefer);
    }

    public Refer<T> decorateFilter(Refer<T> refer) {
        //TODO 使用过滤器装饰
        return refer;
    }
}
