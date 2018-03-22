package com.ppdai.framework.raptor.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public class RpcContext {
    private Map<Object, Object> attributes = new HashMap<>();
    private Map<String, String> requestAttachments = new HashMap<>();
    private Map<String, String> responseAttachments = new HashMap<>();
    private Request request;
    private Response response;

    private static final ThreadLocal<RpcContext> CONTEXT = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return CONTEXT.get();
    }

    /**
     * 初始化RpcContext，用于线程之间传递
     *
     * @param context
     * @return
     */
    public static RpcContext init(RpcContext context) {
        CONTEXT.set(context);
        return context;
    }

    public void putAttribute(Object key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    public void revomeAttribute(Object key) {
        attributes.remove(key);
    }

    public Map<Object, Object> getAttributes() {
        return attributes;
    }

    public void putRequestAttachment(String key, String value) {
        requestAttachments.put(key, value);
    }

    public void putAllRequestAttachments(Map<String, String> map) {
        requestAttachments.putAll(map);
    }

    public String getRequestAttachment(String key) {
        return requestAttachments.get(key);
    }

    public void removeRequestAttachment(String key) {
        requestAttachments.remove(key);
    }

    public Map<String, String> getRequestAttachments() {
        return requestAttachments;
    }


    public void putResponseAttachment(String key, String value) {
        responseAttachments.put(key, value);
    }

    public void putAllResponseAttachments(Map<String, String> map) {
        responseAttachments.putAll(map);
    }

    public String getResponseAttachment(String key) {
        return responseAttachments.get(key);
    }

    public void removeResponseAttachment(String key) {
        responseAttachments.remove(key);
    }

    public Map<String, String> getResponseAttachments() {
        return responseAttachments;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
