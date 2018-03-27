package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.exception.ErrorProto;
import com.ppdai.framework.raptor.exception.HttpErrorConverter;
import com.ppdai.framework.raptor.exception.RaptorFrameworkException;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.serialize.Serialization;
import com.ppdai.framework.raptor.serialize.SerializationProviders;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServletEndpoint extends HttpServlet implements Endpoint {

    @Getter
    protected Map<String, Provider<?>> providers = new ConcurrentHashMap<>();
    @Getter
    protected URL baseUrl;

    public ServletEndpoint(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void init() {
        try {
            super.init();
        } catch (Exception e) {
            throw new RaptorFrameworkException("ServletEndpoint init error.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        Response response = null;
        Request request = null;
        try {
            String key = getProviderKey(getInterfaceName(httpRequest));
            Provider<?> provider = this.providers.get(key);
            if (provider == null) {
                transportException(new RaptorServiceException("Can not find provider by key: " + key), null, httpRequest, httpResponse);
                return;
            }
            request = convert(httpRequest);
            response = provider.call(request);
            if (response.getException() != null) {
                transportException(response.getException(), response, httpRequest, httpResponse);
            } else {
                transportResponse(request, response, httpRequest, httpResponse);
            }
        } catch (Exception e) {
            transportException(e, response, httpRequest, httpResponse);
            if (request != null) {
                log.error("Raptor request error, request={}", request, e);
            } else {
                log.error("Raptor request error.", e);
            }
        }
    }

    @Override
    public URL export(Provider<?> provider) {
        return export(provider, URL.builder().build());
    }

    @Override
    public URL export(Provider<?> provider, URL serviceUrl) {
        URL newServiceUrl = doExport(provider, serviceUrl);
        String key = getProviderKey(provider.getInterface().getName());
        providers.put(key, provider);
        return newServiceUrl;
    }

    protected URL doExport(Provider<?> provider, URL serviceUrl) {
        URL newServiceUrl = baseUrl.createCopy();
        String basePath = StringUtils.removeEnd(baseUrl.getPath(), RaptorConstants.PATH_SEPARATOR);
        newServiceUrl.setProtocol(serviceUrl.getProtocol());
        newServiceUrl.setPath(provider.getInterface().getName());
        newServiceUrl.getParameters().putAll(serviceUrl.getParameters());
        newServiceUrl.addParameter(URLParamType.basePath.name(), basePath);
        return newServiceUrl;
    }

    protected Request convert(HttpServletRequest httpRequest) {
        DefaultRequest request = new DefaultRequest();
        request.setRequestId(getRequestId(httpRequest));
        request.setInterfaceName(getInterfaceName(httpRequest));
        request.setMethodName(getMethodName(httpRequest));
        Map<String, String> attachments = getAttachments(httpRequest);
        request.setAttachments(attachments);

        Provider<?> provider = this.providers.get(getProviderKey(request));
        if (provider != null) {
            request.setArgument(getRequestArgument(httpRequest, request, provider));
        }
        return request;
    }

    protected String getRequestId(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(URLParamType.requestId.name());
    }

    protected String getInterfaceName(HttpServletRequest httpRequest) {
        String path = StringUtils.substringBeforeLast(httpRequest.getPathInfo(), RaptorConstants.PATH_SEPARATOR);
        return StringUtils.substringAfterLast(path, RaptorConstants.PATH_SEPARATOR);
    }

    protected String getMethodName(HttpServletRequest httpRequest) {
        return StringUtils.substringAfterLast(httpRequest.getPathInfo(), RaptorConstants.PATH_SEPARATOR);
    }

    protected Map<String, String> getAttachments(HttpServletRequest httpRequest) {
        Map<String, String> attachments = new HashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headName);
            attachments.put(headName, headerValue);
        }
        return attachments;
    }

    protected Object getRequestArgument(HttpServletRequest httpRequest, Request request, Provider<?> provider) {
        Method method = provider.lookupMethod(request.getMethodName(), null);
        if (method == null) {
            throw new RaptorServiceException(String.format("Can not find method %s/%s", request.getInterfaceName(), request.getMethodName()));
        }
        if (method.getParameterCount() == 0) {
            return null;
        } else if (method.getParameterCount() == 1) {
            Serialization serialization = this.getSerialization(httpRequest);
            try {
                byte[] data = IOUtils.toByteArray(httpRequest.getInputStream());
                return serialization.deserialize(data, method.getParameterTypes()[0]);
            } catch (IOException e) {
                throw new RaptorServiceException("Deserialize request parameter error.", e);
            }
        } else {
            throw new RaptorServiceException(String.format("Interface method %s#%s parameter count must less or equal 1.", request.getInterfaceName(), method.getName()));
        }
    }

    protected Serialization getSerialization(HttpServletRequest httpRequest) {
        String serializationName = httpRequest.getContentType();
        if (StringUtils.isNotBlank(serializationName)) {
            serializationName = StringUtils.substringBefore(serializationName, RaptorConstants.SEPARATOR_CONTENT_TYPE).trim();
            return SerializationProviders.getInstance().getSerialization(serializationName);
        }
        return SerializationProviders.getInstance().getDefault();
    }

    protected String getProviderKey(Request request) {
        return getProviderKey(request.getInterfaceName());
    }


    protected void transportResponse(Request request, Response response, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        setHttpResponseHeader(response, httpResponse);
        httpResponse.setStatus(RaptorConstants.HTTP_OK);
        Serialization serialization = this.getSerialization(httpRequest);
        httpResponse.setHeader(HTTP.CONTENT_TYPE,serialization.getName());
        byte[] data = serialization.serialize(response.getValue());
        try (OutputStream out = httpResponse.getOutputStream()) {
            out.write(data);
            out.flush();
        }
    }

    protected void transportException(Throwable exception, Response response, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        setHttpResponseHeader(response, httpResponse);
        httpResponse.setStatus(HttpErrorConverter.getHttpStatusCode(exception));
        httpResponse.setHeader(ParamNameConstants.RAPTOR_ERROR, RaptorConstants.TRUE);
        ErrorProto.ErrorMessage errorMessage = HttpErrorConverter.getErrorMessage(exception);

        Serialization serialization = this.getSerialization(httpRequest);
        httpResponse.setHeader(HTTP.CONTENT_TYPE,serialization.getName());
        byte[] data = serialization.serialize(errorMessage);
        try (OutputStream out = httpResponse.getOutputStream()) {
            out.write(data);
            out.flush();
        }
    }

    protected void setHttpResponseHeader(Response response, HttpServletResponse httpResponse) {
        RpcContext context = RpcContext.getContext();
        for (Map.Entry<String, String> entry : context.getResponseAttachments().entrySet()) {
            httpResponse.addHeader(entry.getKey(), entry.getValue());
        }
        if (response != null && response.getAttachments() != null) {
            for (Map.Entry<String, String> entry : response.getAttachments().entrySet()) {
                httpResponse.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    protected String getProviderKey(String interfaceName) {
        return interfaceName;
    }

}
