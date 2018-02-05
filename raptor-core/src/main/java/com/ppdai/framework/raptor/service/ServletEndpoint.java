package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.serialize.ProtobufSerializationFactory;
import com.ppdai.framework.raptor.serialize.Serialization;
import com.ppdai.framework.raptor.serialize.SerializationFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServletEndpoint extends HttpServlet implements Endpoint {

    @Getter
    protected Map<String, Provider<?>> providers = new ConcurrentHashMap<>();
    @Setter
    protected SerializationFactory serializationFactory;
    @Getter
    protected URL baseUrl;

    public ServletEndpoint(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        if (this.serializationFactory == null)
            this.serializationFactory = new ProtobufSerializationFactory();
    }

    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        String key = getProviderKey(getInterfaceName(httpRequest), getVersion(httpRequest));
        Provider<?> provider = this.providers.get(key);
        Request request = convert(httpRequest);
        if (provider == null) {
            transportException(new RaptorServiceException("Can not find provider by key: " + key), request, httpResponse);
            return;
        }
        Response response;
        try {
            response = provider.call(request);
            if (response.getException() != null) {
                transportException(response.getException(), request, httpResponse);
            } else {
                transportResponse(request, response, httpRequest, httpResponse);
            }
        } catch (Exception e) {
            transportException(e, request, httpResponse);
            log.error("Request error, requestId={}", request, e);
        }
    }

    @Override
    public URL export(Provider<?> provider) {
        return export(provider, URL.builder().build());
    }

    @Override
    public URL export(Provider<?> provider, URL serviceUrl) {
        URL newServiceUrl = doExport(provider, serviceUrl);
        String key = getProviderKey(provider.getInterface().getName(), newServiceUrl.getParameter(URLParamType.version.name()));
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
        request.setRequestId(URLParamType.requestId.name());
        request.setInterfaceName(getInterfaceName(httpRequest));
        request.setMethodName(getMethodName(httpRequest));
        request.setAttachments(getAttachments(httpRequest));

        RpcContext.getContext().setRequest(request);
        Provider<?> provider = this.providers.get(getProviderKey(request));
        if (provider != null) {
            request.setArguments(getRequestArguments(httpRequest, request, provider));
        }
        return request;
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
        attachments.put(URLParamType.clientHost.getName(), httpRequest.getRemoteAddr());
        return attachments;
    }

    protected Object getRequestArguments(HttpServletRequest httpRequest, Request request, Provider<?> provider) {
        Method method = provider.lookupMethod(request.getMethodName());
        if (method == null) {
            throw new RaptorServiceException(String.format("Can not find method %s#%s", request.getInterfaceName(), request.getMethodName()));
        }
        if (method.getParameterCount() == 0) {
            return new Object[0];
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
        return this.serializationFactory.newInstance(httpRequest.getHeader(URLParamType.serialization.getName()));
    }

    protected String getVersion(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(URLParamType.version.name());
    }

    protected String getProviderKey(Request request) {
        return getProviderKey(request.getInterfaceName(), MapUtils.getString(request.getAttachments(), URLParamType.version.name()));
    }

    protected void transportResponse(Request request, Response response, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        httpResponse.setStatus(RaptorConstants.HTTP_OK);
        try (OutputStream out = httpResponse.getOutputStream()) {
            Serialization serialization = this.getSerialization(httpRequest);
            byte[] data = serialization.serialize(response.getValue());
            out.write(data);
            out.flush();
        } catch (IOException e) {
            log.error("write response error.request: {}", request, e);
        }
    }

    protected void transportException(Exception e, Request request, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(RaptorConstants.HTTP_EXPECTATION_FAILED);
        httpServletResponse.setHeader(URLParamType.exceptionClassHeader.getName(), e.getClass().getName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStream out = httpServletResponse.getOutputStream()) {
            e.printStackTrace(new PrintStream(baos));
            byte[] data = baos.toByteArray();
            out.write(data);
            out.flush();
        } catch (Exception e1) {
            log.error("write response error.request: {}", request, e);
        }
    }

    protected String getProviderKey(String interfaceName, String version) {
        return StringUtils.isBlank(version) ? interfaceName : interfaceName + "?version=" + version;
    }

}
