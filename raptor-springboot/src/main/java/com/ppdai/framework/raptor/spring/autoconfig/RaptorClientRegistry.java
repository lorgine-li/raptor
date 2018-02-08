package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.exception.RaptorFrameworkException;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.refer.ReferBuilder;
import com.ppdai.framework.raptor.refer.proxy.JdkProxyFactory;
import com.ppdai.framework.raptor.refer.proxy.ReferInvocationHandler;
import com.ppdai.framework.raptor.refer.repository.UrlRepository;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class RaptorClientRegistry {

    private UrlRepository urlRepository;
    private ReferBuilder referBuilder;

    private Map<String, Object> clientCache = new ConcurrentHashMap<>();

    public RaptorClientRegistry(UrlRepository urlRepository, ReferBuilder referBuilder) {
        this.urlRepository = urlRepository;
        this.referBuilder = referBuilder;
    }


    public Object getOrCreateClientProxy(String interfaceName, String url) {
        URL serviceUrl = getURL(interfaceName, url);
        if (serviceUrl == null) {
            throw new RaptorServiceException(String.format("Can't find service url of interface: %s", interfaceName));
        }
        String cacheKey = getCacheKey(interfaceName, serviceUrl);
        Object proxy = clientCache.get(cacheKey);
        if (proxy == null) {
            proxy = createClientProxy(interfaceName, serviceUrl);
            clientCache.put(cacheKey, proxy);
        }
        return proxy;
    }

    public URL getURL(String interfaceName, String url) {
        URL serviceUrl;
        if (StringUtils.isNotBlank(url)) {
            serviceUrl = URL.valueOf(url);
            if (!serviceUrl.isValid()) {
                throw new RaptorServiceException("url is not valid: " + url);
            }
        } else {
            serviceUrl = urlRepository.getUrl(interfaceName);
        }
        return serviceUrl;
    }

    public Object createClientProxy(String interfaceName, URL serviceUrl) {
        Class<?> clazz;
        try {
            clazz = ReflectUtil.forName(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new RaptorFrameworkException("Can't find class " + interfaceName);
        }

        Refer<?> refer = referBuilder.build(clazz, serviceUrl);
        ReferInvocationHandler invocationHandler = new ReferInvocationHandler(clazz, refer);
        return new JdkProxyFactory().getProxy(clazz, invocationHandler);
    }

    private String getCacheKey(String interfaceName, URL serviceUrl) {
        return interfaceName + "@" + serviceUrl.toFullStr();
    }
}
