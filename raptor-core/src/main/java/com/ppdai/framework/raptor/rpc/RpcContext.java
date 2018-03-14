package com.ppdai.framework.raptor.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public class RpcContext {
    private Map<Object, Object> attributes = new HashMap<>();
    private Map<String, String> attachments = new HashMap<>();
    private Request request;
    private Response response;
    private String clientRequestId = null;

    private static final ThreadLocal<RpcContext> LOCAL_CONTEXT = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return LOCAL_CONTEXT.get();
    }

    public static RpcContext init(Request request) {
        RpcContext context = new RpcContext();
        if (request != null) {
            context.setRequest(request);
            String requestIdFromClient = String.valueOf(request.getRequestId());
            context.setClientRequestId(requestIdFromClient);
        }
        LOCAL_CONTEXT.set(context);
        return context;
    }

    public static RpcContext init() {
        RpcContext context = new RpcContext();
        LOCAL_CONTEXT.set(context);
        return context;
    }

    public static void destroy() {
        LOCAL_CONTEXT.remove();
    }

    public String getRequestId() {
        if (clientRequestId != null) {
            return clientRequestId;
        } else {
            return request == null ? null : String.valueOf(request.getRequestId());
        }
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

    public void setRpcAttachment(String key, String value) {
        attachments.put(key, value);
    }

    public String getRpcAttachment(String key) {
        return attachments.get(key);
    }

    public void removeRpcAttachment(String key) {
        attachments.remove(key);
    }

    public Map<String, String> getRpcAttachments() {
        return attachments;
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

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

}
