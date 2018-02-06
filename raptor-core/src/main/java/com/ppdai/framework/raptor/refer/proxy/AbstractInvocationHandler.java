package com.ppdai.framework.raptor.refer.proxy;

import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.DefaultRequest;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.RpcContext;
import com.ppdai.framework.raptor.util.RaptorFrameworkUtil;
import com.ppdai.framework.raptor.util.ExceptionUtil;
import com.ppdai.framework.raptor.util.RequestIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public abstract class AbstractInvocationHandler<T> implements InvocationHandler {

    protected Class<T> interfaceClass;

    public AbstractInvocationHandler(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isLocalMethod(method)) {
            this.invokeLocal(method, args);
        }

        DefaultRequest request = new DefaultRequest();
        request.setRequestId(RequestIdGenerator.getRequestId());
        request.setInterfaceName(this.interfaceClass.getName());
        request.setMethodName(method.getName());
        if (args.length == 0) {
            request.setArgument(null);
        } else if (args.length == 1) {
            request.setArgument(args[0]);
        } else {
            throw new RaptorServiceException("Method arguments has more then one.");
        }
        request.setReturnType(method.getReturnType().getName());

        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setRequest(request);
        try {
            return doInvoke(request);
        } catch (RuntimeException e) {
            if (ExceptionUtil.isBizException(e)) {
                Throwable t = e.getCause();
                // 只抛出Exception，防止抛出远程的Error
                if (t != null && t instanceof Exception) {
                    throw t;
                } else {
                    String msg = t == null
                            ? "Biz exception cause is null. origin error message : " + e.getMessage()
                            : ("Biz exception cause is throwable error:" + t.getClass() + ", error message:" + t.getMessage());
                    throw new RaptorServiceException(msg);
                }
            } else {
                log.error("InvocationHandler invoke Error: interface={}, request={}", request.getInterfaceName(), RaptorFrameworkUtil.toString(request), e);
                throw e;
            }
        }
    }

    protected boolean isLocalMethod(Method method) {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return false;
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    public abstract Object doInvoke(Request request);

    protected abstract Object invokeLocal(Method method, Object[] args) throws Exception;
}
