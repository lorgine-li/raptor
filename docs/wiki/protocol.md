Raptor RPC协议说明
# 概览
Raptor协议是基于HTTP和Protocal Buffers的简单RPC协议。本协议使用HTTP URLs定义RPC端点，HTTP的request/response的body用于发送/接收 proto的Message 。

使用Raptor，首先需要使用proto文件定义API，然后使用raptor工具生成客户端和服务端的依赖的RPC接口，服务端实现该接口的业务逻辑，客户端通过Raptor提供的工具生成客户端的代理访问服务端的RPC调用。

Raptor支持proto message的二进制和json的序列化。

## URLs
根据 [BNF syntax](https://tools.ietf.org/html/rfc5234)语法，Raptor的URLs格式如下：
```
URL ::= Base-URL "/" [ Package "." ] Service "/" Method
```
Raptor协议使用HTTP URLs在服务端定义请求的RPC端点，这种定义方式简单而且高效，包含如下几个部分：

- **Base-URL** Raptor服务端的虚拟路径
- **Package** proto中定义API的`package`名称
- **Service** proto中定义API的`service`名称
- **Method** proto中`service`下定义的`rpc`方法名称

## Requests
Raptor只能使用HTTP POST方法发送请求，因为它与RPC方法的语义匹配的最紧密。

HTTP请求头使用 **Content-Type** 指定proto message的序列化方式，`application/protobuf`为proto二进制方式序列化， `application/json`为json序列化方式，序列化结果放在Request body中。

## Responses
HTTP响应头也使用 **Content-Type** 来指定响应结果的序列化方式，`application/protobuf`为proto二进制方式序列化， `application/json`为json序列化方式，序列化结果放在Response body中。

## 示例
以下示例是一个简单的echo API定义。

假设本示例服务端的base URL为http://example.com/raptor
```
syntax = "proto3";

package example.echoer;

service Echo {
  rpc Hello(HelloRequest) returns (HelloResponse);
}

message HelloRequest {
  string message = 1;
}

message HelloResponse {
  string message = 1;
}
```

**Proto Request**
```
POST /raptor/example.echoer.Echo/Hello HTTP/1.1
Host: example.com
Content-Type: application/protobuf
Content-Length: 15

<encoded HelloRequest>
```

**JSON Request**
```
POST /raptor/example.echoer.Echo/Hello HTTP/1.1
Host: example.com
Content-Type: application/json
Content-Length: 27

{"message":"Hello, World!"}
```

**Proto Response**
```
HTTP/1.1 200 OK
Content-Type: application/protobuf
Content-Length: 15

<encoded HelloResponse>
```

**JSON Response**
```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 27

{"message":"Hello, World!"}
```

## 异常
Raptor服务端发生异常后，会将异常信息作为HTTP response返回，由Raptor框架抛出的异常，response的status为500，response body为raptor框架定义的proto message：
```
syntax = "proto3";

package com.ppdai.framework.raptor.exception;

message ErrorMessage {
    int32 code = 1;
    string message = 2;
    map<string, string> attachments = 3;
}
```
- **code:** raptor的错误码
- **message:** 异常消息
- **attachments:** 异常的附加信息

Raptor将异常分为3类：
- **服务异常** 调用RPC服务产生的异常，code范围[10000,19999]，比如服务端没有启动
- **框架异常** Raptor框架本身抛出的异常，code范围[20000,29999]，比如框架中找不到序列化类
- **业务异常** 服务端执行业务逻辑发生的异常，code范围[30000,39999]，通常是在服务端实现API接口中抛出的异常

## 错误码
| 错误码         | 说明            |
| :------------- | :------------- |
| 10000 | 默认服务异常 |
| 10001 | 没有找到指定的服务|
| 19999 | 未知服务异常|
| 20000 | 默认框架异常|
| 30000 | 默认业务异常|
