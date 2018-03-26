package com.ppdai.framework.raptor.refer.client;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.exception.ErrorProto;
import com.ppdai.framework.raptor.exception.RaptorBizException;
import com.ppdai.framework.raptor.exception.RaptorFrameworkException;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.*;
import com.ppdai.framework.raptor.serialize.Serialization;
import com.ppdai.framework.raptor.serialize.SerializationProviders;
import com.ppdai.framework.raptor.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@Slf4j
public abstract class AbstractHttpClient implements Client {

    private Serialization serialization;

    @Override
    public void init() {
        if (this.serialization == null) {
            this.serialization = SerializationProviders.getInstance().getDefault();
        }
    }

    @Override
    public Response sendRequest(Request request, URL serviceUrl) {
        DefaultResponse response = new DefaultResponse(request.getRequestId());

        HttpResponse httpResponse = null;
        HttpPost httpPost = null;
        try {
            httpPost = buildHttpPost(request, serviceUrl);

            buildHeaders(request, serviceUrl, httpPost);

            httpResponse = doSendRequest(httpPost, serviceUrl);
            //设置响应头
            setResponseHeaders(response, httpResponse);

            //设置status
            StatusLine statusLine = httpResponse.getStatusLine();
            setStatus(response, statusLine);

            setResponseValue(request, response, httpResponse);

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

        String serializationType = this.serialization.getName();
        ContentType contentType = ContentType.create(serializationType);

        HttpEntity entity;
        if (Objects.isNull(request.getArgument())) {
            entity = EntityBuilder.create().build();
        } else {
            byte[] data = this.buildContent(request);
            entity = EntityBuilder.create().setBinary(data).setContentType(contentType).build();
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

    protected void buildHeaders(Request request, URL serviceUrl, HttpRequestBase httpRequest) {
        //header优先级 context > request > default
        RpcContext context = RpcContext.getContext();
        for (Map.Entry<String, String> entry : context.getRequestAttachments().entrySet()) {
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : request.getAttachments().entrySet()) {
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
        String requestId = String.valueOf(request.getRequestId());
        httpRequest.addHeader(URLParamType.requestId.name(), requestId);
        httpRequest.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
    }

    protected byte[] buildContent(Request request) {
        return this.serialization.serialize(request.getArgument());
    }

    protected void setResponseValue(Request request, DefaultResponse response, HttpResponse httpResponse) {
        //设置响应body
        HttpEntity entity = httpResponse.getEntity();
        byte[] content;
        try {
            content = IOUtils.toByteArray(entity.getContent());
        } catch (IOException e) {
            throw new RaptorServiceException("Error read response content, requestId=" + response.getRequestId(), e);
        }
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        Header raptorError = httpResponse.getFirstHeader(ParamNameConstants.RAPTOR_ERROR);
        String errorStr = Objects.isNull(raptorError) ? null : raptorError.getValue();
        //  根据http状态码做不同的处理
        if (statusCode == RaptorConstants.HTTP_OK && !RaptorConstants.TRUE.equalsIgnoreCase(errorStr)) {
            response.setValue(deserializeResponseValue(request, content));
            response.setCode(RaptorMessageConstant.SUCCESS);
            return;
        }
        if (statusCode == RaptorConstants.RAPTOR_ERROR && RaptorConstants.TRUE.equalsIgnoreCase(errorStr)) {
            //raptor异常
            try {
                ErrorProto.ErrorMessage errorMessage = deserializeErrorMessage(request, content);
                response.setException(getExceptionByErrorMessage(errorMessage));
                response.setCode(errorMessage.getCode());
                return;
            } catch (Exception e) {
                log.error("Can not serialize raptor error:\n{}", new String(content, StandardCharsets.UTF_8));
            }
        }
        RaptorServiceException e = new RaptorServiceException(new String(content, StandardCharsets.UTF_8));
        response.setException(e);
        response.setCode(e.getCode());
    }

    protected ErrorProto.ErrorMessage deserializeErrorMessage(Request request, byte[] content) {
        try {
            return this.serialization.deserialize(content, ErrorProto.ErrorMessage.class);
        } catch (Exception e) {
            throw new RaptorServiceException("Deserialize response ErrorMessage error, requestId=" + request.getRequestId(), e);
        }
    }

    protected Object deserializeResponseValue(Request request, byte[] content) {
        String returnType = request.getReturnType();
        try {
            Class<?> returnClass = ReflectUtil.forName(returnType);
            return this.serialization.deserialize(content, returnClass);
        } catch (Exception e) {
            throw new RaptorServiceException("Deserialize response content error, requestId=" + request.getRequestId(), e);
        }
    }

    protected Exception getExceptionByErrorMessage(ErrorProto.ErrorMessage errorMessage) {
        if (errorMessage == null) {
            return new RaptorServiceException("Unknow exception, errorMessage is null");
        }
        int code = errorMessage.getCode();
        if (code >= RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE && code < RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR_CODE) {
            return new RaptorServiceException(errorMessage.getCode(), errorMessage.getMessage(), errorMessage.getAttachmentsMap(), null);
        } else if (code >= RaptorMessageConstant.FRAMEWORK_DEFAULT_ERROR_CODE && code < RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE) {
            return new RaptorFrameworkException(errorMessage.getCode(), errorMessage.getMessage(), errorMessage.getAttachmentsMap(), null);
        } else if (code >= RaptorMessageConstant.BIZ_DEFAULT_ERROR_CODE) {
            return new RaptorBizException(errorMessage.getCode(), errorMessage.getMessage(), errorMessage.getAttachmentsMap(), null);
        }

        return new RaptorServiceException(errorMessage.getCode(), errorMessage.getMessage(), errorMessage.getAttachmentsMap(), null);
    }

    protected void setStatus(Response response, StatusLine statusLine) {
        response.setAttachment(URLParamType.httpVersion.name(), statusLine.getProtocolVersion().toString());
        response.setAttachment(URLParamType.httpStatusCode.name(), String.valueOf(statusLine.getStatusCode()));
        response.setAttachment(URLParamType.httpReasonPhrase.name(), statusLine.getReasonPhrase());
    }

    protected void setResponseHeaders(Response response, HttpResponse httpResponse) {
        List<Header> headers = Arrays.asList(httpResponse.getAllHeaders());
        for (Header header : headers) {
            response.setAttachment(header.getName(), header.getValue());
        }
    }
}
