package com.ppdai.framework.raptor.rpc;

import com.ppdai.framework.raptor.util.StringTools;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定义服务资源信息
 */
@Builder
public class URL {

    private String protocol;

    private String host;

    private int port;

    private String path;

    private Map<String, String> parameters;

    final private transient Map<String, Number> numbers = new ConcurrentHashMap<>();

    public URL(String protocol, String host, int port, String path) {
        this(protocol, host, port, path, new HashMap<>());
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
        if (parameters == null) {
            this.parameters = new HashMap<>();
        } else {
            this.parameters = parameters;
        }
    }

    public static URL valueOf(String url) {
        if (StringUtils.isBlank(url)) {
            throw new RaptorServiceException("serviceUrl is null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = new HashMap<>();
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");

            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("serviceUrl missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("serviceUrl missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }

        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, host, port, path, parameters);
    }

    public boolean isValid() {
        return StringUtils.isNoneBlank(this.host);
    }

    public URL createCopy() {
        Map<String, String> params = new HashMap<>();
        if (this.parameters != null) {
            params.putAll(this.parameters);
        }

        return new URL(protocol, host, port, path, params);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return getParameter(URLParamType.version.getName());
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getParameter(String name, String defaultValue) {
        String value = getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void addParameter(String name, String value) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(value)) {
            return;
        }
        parameters.put(name, value);
    }

    public void removeParameter(String name) {
        if (name != null) {
            parameters.remove(name);
        }
    }

    public void addParameters(Map<String, String> params) {
        if (params != null) {
            parameters.putAll(params);
        }
    }

    public void addParameterIfAbsent(String name, String value) {
        if (hasParameter(name)) {
            return;
        }
        parameters.put(name, value);
    }

    public Boolean getBooleanParameter(String name, boolean defaultValue) {
        String value = getParameter(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    public Integer getIntParameter(String name, int defaultValue) {
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.intValue();
        }
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(name, i);
        return i;
    }

    public Long getLongParameter(String name, long defaultValue) {
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.longValue();
        }
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        getNumbers().put(name, l);
        return l;
    }

    public Float getFloatParameter(String name, float defaultValue) {
        Number n = getNumbers().get(name);
        if (n != null) {
            return n.floatValue();
        }
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        getNumbers().put(name, f);
        return f;
    }

    public Boolean getBooleanParameter(String name) {
        String value = parameters.get(name);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    public String getUri() {
        String uri = "";
        if (StringUtils.isNotBlank(this.protocol)) {
            uri += this.protocol + RaptorConstants.PROTOCOL_SEPARATOR;
        }
        uri += StringUtils.defaultString(host);
        if (this.port > 0) {
            uri += RaptorConstants.HOST_PORT_SEPARATOR + this.port;
        }
        if (StringUtils.isNotBlank(path)) {
            uri += RaptorConstants.PATH_SEPARATOR + StringUtils.removeStart(path, RaptorConstants.PATH_SEPARATOR);
        }
        return uri;
    }

    public String toFullStr() {
        String fullStr = getUri();
        ArrayList<String> keys = new ArrayList<>(parameters.keySet());
        //参数排序
        keys.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        ArrayList<String> parameterPairs = new ArrayList<>();
        for (String key : keys) {
            String name = StringTools.urlEncode(key);
            String value = StringTools.urlEncode(parameters.get(key));
            parameterPairs.add(name + "=" + value);
        }
        if (parameterPairs.size() > 0) {
            fullStr += "?" + StringUtils.join(parameterPairs.toArray(), "&");
        }
        return fullStr;
    }

    public String toString() {
        return getUri();
    }


    public boolean hasParameter(String key) {
        return StringUtils.isNotBlank(getParameter(key));
    }

    @Override
    public int hashCode() {
        int factor = 31;
        int rs = 1;
        rs = factor * rs + ObjectUtils.hashCode(protocol);
        rs = factor * rs + ObjectUtils.hashCode(host);
        rs = factor * rs + ObjectUtils.hashCode(port);
        rs = factor * rs + ObjectUtils.hashCode(path);
        rs = factor * rs + ObjectUtils.hashCode(parameters);
        return rs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof URL)) {
            return false;
        }
        URL ou = (URL) obj;
        if (!ObjectUtils.equals(this.protocol, ou.protocol)) {
            return false;
        }
        if (!ObjectUtils.equals(this.host, ou.host)) {
            return false;
        }
        if (!ObjectUtils.equals(this.port, ou.port)) {
            return false;
        }
        if (!ObjectUtils.equals(this.path, ou.path)) {
            return false;
        }
        return ObjectUtils.equals(this.parameters, ou.parameters);
    }

    private Map<String, Number> getNumbers() {
        return numbers;
    }

}
