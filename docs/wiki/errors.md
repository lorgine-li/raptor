# 错误处理
Raptor异常处理参考 [协议说明](protocol/#异常)

Raptor会将服务端抛出的异常信息作为HTTP response返回，response的status为500，response body为raptor框架定义的proto message：
```
syntax = "proto3";

package com.ppdai.framework.raptor.exception;

message ErrorMessage {
    int32 code = 1;
    string message = 2;
    map<string, string> attachments = 3;
}
```

Raptor定义的`RaptorBizException`作为业务异常，该类同样包含`code`,`message`,`attachments`三个属性，服务端抛出该异常，会作为`ErrorMessage`格式序列化在Response Body中返回客户端。Raptor java客户端会反过来将`ErrorMessage`转换为`RaptorBizException`抛出。



服务端抛出异常：
```
  @Override
  public RespMessage service(ReqMessage request) {
      throw new RaptorBizException(30001,"service error.", null)
              .putAttachment("myException", "myValue");
  }
```

客户端catch异常：
```
try {
   proxy.service(request);
} catch (RaptorBizException e) {
   e.getMessage();
   e.getCode();
   e.getAttachments();
}
```

 > **约定：** Raptor定义业务异常`RaptorBizException`的errorCode必须大于30000，如果小于30000，传递给客户端是会设置成30000
