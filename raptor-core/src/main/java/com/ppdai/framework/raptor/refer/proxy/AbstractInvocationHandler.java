package com.ppdai.framework.raptor.refer.proxy;

import com.ppdai.framework.raptor.common.ParamNameConstants;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.DefaultRequest;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.RpcContext;
import com.ppdai.framework.raptor.util.NetUtils;
import com.ppdai.framework.raptor.util.RaptorFrameworkUtil;
import com.ppdai.framework.raptor.util.RequestIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public abstract class AbstractInvocationHandler implements InvocationHandler {

    protected Class<?> interfaceClass;

    public AbstractInvocationHandler(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isLocalMethod(method)) {
            return this.invokeLocal(method, args);
        }

        //TODO 将这块代码提取出来，写到builder中
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
        request.setAttachment(ParamNameConstants.CLIENT_HOST, NetUtils.getLocalIp());

        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setRequest(request);
        try {
            return doInvoke(request);
        } catch (Exception e) {
            log.error("InvocationHandler invoke Error: interface={}, request={}", request.getInterfaceName(), RaptorFrameworkUtil.toString(request), e);
            throw e;
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
