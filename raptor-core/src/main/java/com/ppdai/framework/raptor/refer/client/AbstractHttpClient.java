package com.ppdai.framework.raptor.refer.client;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.exception.*;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.serialize.ProtobufSerializationFactory;
import com.ppdai.framework.raptor.serialize.Serialization;
import com.ppdai.framework.raptor.serialize.SerializationFactory;
import com.ppdai.framework.raptor.util.ExceptionUtil;
import com.ppdai.framework.raptor.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Setter
@Getter
@Slf4j
public abstract class AbstractHttpClient implements Client {

    private SerializationFactory serializationFactory;

    @Override
    public void init() {
        if (this.serializationFactory == null) {
            this.serializationFactory = new ProtobufSerializationFactory();
        }
    }

    @Override
    public Response sendRequest(Request request, URL serviceUrl) {
        DefaultResponse response = new DefaultResponse(request.getRequestId());
        HttpResponse httpResponse = null;
        HttpPost httpPost = null;
        try {
            httpPost = buildHttpPost(request, serviceUrl);

            for (Header header : buildHeaders(request, serviceUrl)) {
                httpPost.addHeader(new BasicHeader(header.getName(), header.getValue()));
            }

            httpResponse = doSendRequest(httpPost, serviceUrl);

            //设置响应头
            List<Header> headers = Arrays.asList(httpResponse.getAllHeaders());
            setResponseHeaders(response, headers);

            //设置status
            StatusLine statusLine = httpResponse.getStatusLine();
            setStatus(response, statusLine);

            //设置响应body
            HttpEntity entity = httpResponse.getEntity();
            byte[] content;
            try {
                content = IOUtils.toByteArray(entity.getContent());
            } catch (IOException e) {
                throw new RaptorServiceException("Error read response content, requestId=" + response.getRequestId(), e);
            }
            setResponseValue(request, response, statusLine.getStatusCode(), content);

        } catch (Exception e) {
            response.setException(e);
        } finally {
            try {
                if (httpResponse != null && httpResponse instanceof Closeable) {
                    ((Closeable) httpResponse).close();
                }
                if (httpPost != null) {
                    httpPost.releaseConnection();
                }
            } catch (IOException e) {
                log.warn("Error close httpResponse, requestId={}", request.getRequestId(), e);
            }
        }
        return response;
    }

    public HttpPost buildHttpPost(Request request, URL serviceUrl) {
        URI uri = buildUri(request, serviceUrl);
        HttpPost post = new HttpPost(uri);

        HttpEntity entity;
        if (Objects.isNull(request.getArgument())) {
            entity = EntityBuilder.create().build();
        } else {
            byte[] data = this.buildContent(request, serviceUrl);
            entity = EntityBuilder.create().setBinary(data).build();
        }
        post.setEntity(entity);
        return post;
    }


    protected abstract HttpResponse doSendRequest(HttpPost httpPost, URL serviceUrl) throws IOException;

    protected URI buildUri(Request request, URL serviceUrl) {
        try {
            String protocol = StringUtils.isNotBlank(serviceUrl.getProtocol()) ? serviceUrl.getProtocol() : RaptorConstants.HTTP;

            String path = buildPath(request, serviceUrl);

            int port = serviceUrl.getPort() > 0 ? serviceUrl.getPort() : -1;

            return new URI(protocol, null,
                    serviceUrl.getHost(), port, path, null, null);
        } catch (Exception e) {
            throw new RaptorFrameworkException("build request uri error.", e);
        }
    }

    protected String buildPath(Request request, URL serviceUrl) {
        //前后的'/'去掉
        String path = StringUtils.removeStart(serviceUrl.getPath(), RaptorConstants.PATH_SEPARATOR);
        path = StringUtils.removeEnd(path, RaptorConstants.PATH_SEPARATOR);

        if (StringUtils.isBlank(path)) {
            path = URLParamType.basePath.getValue() + RaptorConstants.PATH_SEPARATOR + request.getInterfaceName();
        }
        //前面加上'/'
        path = RaptorConstants.PATH_SEPARATOR + path + RaptorConstants.PATH_SEPARATOR + request.getMethodName();

        return path;

    }

    protected List<Header> buildHeaders(Request request, URL serviceUrl) {
        List<Header> headers = new ArrayList<>();
        Map<String, String> attachments = request.getAttachments();
        for (String key : attachments.keySet()) {
            Header header = new BasicHeader(key, attachments.get(key));
            headers.add(header);
        }
        String requestId = String.valueOf(request.getRequestId());
        headers.add(new BasicHeader(URLParamType.serialization.getName(), getSerializationType(request, serviceUrl)));
        headers.add(new BasicHeader(URLParamType.requestId.name(), requestId));
        headers.add(new BasicHeader("connection", "Keep-Alive"));
        return headers;
    }

    protected String getSerializationType(Request request, URL serviceUrl) {
        String key = URLParamType.serialization.getName();
        // serializationType获取顺序 request.getAttachments > RpcContext > serviceUrl > URLParamType.serialization
        String serializationType = request.getAttachments().get(key);
        serializationType = StringUtils.isBlank(serializationType) ? RpcContext.getContext().getRpcAttachment(key) : serializationType;
        serializationType = StringUtils.isBlank(serializationType) ? serviceUrl.getParameter(key) : serializationType;
        serializationType = StringUtils.isBlank(serializationType) ? URLParamType.serialization.getValue() : serializationType;
        return serializationType;
    }

    protected byte[] buildContent(Request request, URL serviceUrl) {
        Serialization serialization = this.buildSerialization(request);
        return serialization.serialize(request.getArgument());
    }

    protected Serialization buildSerialization(Request request) {
        String serializationParam = MapUtils.getString(request.getAttachments(),
                URLParamType.serialization.getName(), URLParamType.serialization.getValue());
        return this.getSerializationFactory().newInstance(serializationParam);
    }

    protected void setResponseValue(Request request, DefaultResponse response, int statusCode, byte[] content) {
        //  根据http状态码做不同的处理
        if (statusCode >= 200 && statusCode < 300) {
            response.setValue(deserializeResponseValue(request, content));
            response.setCode(RaptorMessageConstant.SUCCESS);
        } else if (statusCode >= 300 && statusCode < 400) {
            response.setException(new RaptorServiceException(new RaptorMessage(statusCode,
                    RaptorMessageConstant.SERVICE_REDIRECT_ERROR_CODE, "Http request redirect error.")));
            response.setCode(RaptorMessageConstant.SERVICE_REDIRECT_ERROR_CODE);
        } else if (statusCode == 404) {
            response.setException(new RaptorServiceException(RaptorMessageConstant.SERVICE_UNFOUND));
            response.setCode(RaptorMessageConstant.SERVICE_UNFOUND_ERROR_CODE);
        } else if (statusCode >= 400 && statusCode < 600) {//4xx,5xx处理逻辑是一样的
            String exceptionClass = response.getAttachments().get(URLParamType.exceptionClassHeader.getName());
            Exception e = deserializeResponseException(request, exceptionClass, content);
            int code = RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE;
            if (e == null) {
                e = new RaptorServiceException(new RaptorMessage(statusCode,
                        RaptorMessageConstant.SERVICE_UNKNOW_ERROR_CODE, "Unknow error, status code is " + statusCode));
            } else if (ExceptionUtil.isRaptorException(e)) {
                code = ((RaptorAbstractException) e).getErrorCode();
                code = code == 0 ? RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE : code;
            }
            response.setException(e);
            response.setCode(code);
        } else {
            response.setException(new RaptorServiceException(new RaptorMessage(statusCode,
                    RaptorMessageConstant.SERVICE_UNKNOW_ERROR_CODE, "Unknow error.")));
            response.setCode(RaptorMessageConstant.SERVICE_UNKNOW_ERROR_CODE);
        }
    }

    protected Object deserializeResponseValue(Request request, byte[] content) {
        String returnType = request.getReturnType();
        try {
            Class<?> returnClass = ReflectUtil.forName(returnType);
            Serialization serialization = this.buildSerialization(request);
            return serialization.deserialize(content, returnClass);
        } catch (Exception e) {
            throw new RaptorServiceException("Deserialize response content error, requestId=" + request.getRequestId(), e);
        }
    }


    protected Exception deserializeResponseException(Request request, String exceptionClass, byte[] content) {
        String errorMessage = new String(content, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(exceptionClass)) {
            return new RaptorServiceException(errorMessage);
        }
        if (StringUtils.equals(exceptionClass, RaptorBizException.class.getName())) {
            return new RaptorBizException(errorMessage);
        } else if (StringUtils.equals(exceptionClass, RaptorServiceException.class.getName())) {
            return new RaptorServiceException(errorMessage);
        } else if (StringUtils.equals(exceptionClass, RaptorFrameworkException.class.getName())) {
            return new RaptorFrameworkException(errorMessage);
        } else {
            return new RaptorServiceException(exceptionClass + ": " + errorMessage);
        }
    }

    protected void setStatus(Response response, StatusLine statusLine) {
        response.setAttachment(URLParamType.httpVersion.name(), statusLine.getProtocolVersion().toString());
        response.setAttachment(URLParamType.httpStatusCode.name(), String.valueOf(statusLine.getStatusCode()));
        response.setAttachment(URLParamType.httpReasonPhrase.name(), statusLine.getReasonPhrase());
    }

    protected void setResponseHeaders(Response response, List<Header> headerEntries) {
        for (Header header : headerEntries) {
            response.setAttachment(header.getName(), header.getValue());
        }
    }
}
