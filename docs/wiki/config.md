
# 配置概述
Raptor通常和Spring Boot配套使用。子项目raptor-springboot基于Spring Boot提供自动配置、自动装配的能力，使raptor框架开箱即用。不集成Spring Boot,Raptor也能单独工作,本文档先介绍Spring Boot集成,再介绍原生Raptor如何配置.


## Spring Boot 集成

### 服务端地址
需要在客户端配置各个服务端地址。在`application.properties`中加上如下配置：
```properties
raptor.url.xxx.xxx.XxxApi=http://host:port
```
其中`xxx.xxx.XxxApi`为服务接口的完全限定类名(Fully-Qualified Class Name,FQCN)

> 默认前缀为`raptor.url.`,可以通过`raptor.url.prefix`属性自定义前缀。

### 配置ApacheHttpClient
Raptor默认使用ApacheHttpClient作为Http客户端,可以通过下面这些属性配置ApacheHttpClient:
```properties
raptor.client.apache.connectTimeout = -1
raptor.client.apache.socketTimeout = -1
raptor.client.apache.connectionRequestTimeout = -1
raptor.client.apache.retryCount = 0
raptor.client.apache.requestSentRetryEnabled = false
raptor.client.apache.poolMaxTotal = 100
raptor.client.apache.poolMaxPreRoute = 10
```
参数说明:
| 名称            | 说明           |
| :------------- | :------------- |
|connectTimeout|从连接池获取连接的超时时间,单位毫秒|
|socketTimeout|客户端和服务器建立连接的超时时间，单位毫秒|
|connectionRequestTimeout|客户端从服务器读取数据的超时时间，单位毫秒|
|retryCount|重试次数|
|requestSentRetryEnabled|是否开启重试功能|
|poolMaxTotal|连接池最大线程数|
|poolMaxPreRoute|每个[路由](http://hc.apache.org/httpcomponents-client-4.5.x/httpclient/apidocs/index.html)可拥有最大连接数|


### filter启用和停用
Raptor为服务端和客户端各提供一个AccessLogFilter，和一个MetricFilter。这四个Filter默认全部开启，可以通过如下配置关闭：
```properties
# 关闭服务端accessLog
raptor.provider.filter.accessLog=false
# 关闭服务端 metrics
raptor.provider.filter.metrics=false
# 关闭客户端accessLog
raptor.refer.filter.accessLogg=false
# 关闭客户端 metrics
raptor.refer.filter.metrics=false
```

## 原生Raptor配置

### 服务端配置
Raptor服务器端的配置十分简单,可以参考下面的代码.

1. 创建一个EndPoint,这里我们选择自带Jet统一服务
2. 创建服务实例
3. 根据服务和Filter构建Provider
4. 将Provider暴露给之前创建的EndPoint

```java
public static void main(String[] args) {
    // 1.生成servlet
    URL baseUrl = URL.builder()
            .port(8080)
            .path("/raptor")
            .build();
    servletEndpoint = new JettyServletEndpoint(baseUrl);
    servletEndpoint.start();

    //2.创建服务实例
    Simple simple = new SimpleImpl();
    //3. 根据服务和Filter构建Provider
    Provider<Simple> provider = ProviderBuilder.newBuilder()
               .addFilter(new ProviderAccessLogFilter())//增加filter
               .addFilter(new ProviderMetricsFilter())
               .build(Simple.class, simple);

    //4.将Provider暴露给之前创建的EndPoint
    servletEndpoint.export(provider);
}
```

### 客户端配置
在客户端使用原生Raptor需要手动创建动态代理.我们提供了`ReferProxyBuilder`方便用户创建代理.通过`ReferProxyBuilder`,你可以指定Client,Filter,序列化方式和服务的URL.除了接口名和URL,其他信息都是可选的,不显示设置Raptor会使用默认设置.

```java
public static void main(String[] args) {
    String url = "http://localhost:8080";

    Simple proxy = ReferProxyBuilder.newBuilder()
            .addFilter(new ReferMetricsFilter())
            .serialization(new ProtobufJsonSerialization())
            .build(Simple.class, URL.valueOf(url));

    proxy.sayHello(Helloworld.HelloRequest.newBuilder().build());
}
```

## 通用配置

### 配置URL参数
全局超时时间等属性可以通过配置ApacheHttpClient实现,如果想根据不同服务配置不同超时时间,可以通过URL参数配在URL上.例如,我们想特别配置Simple这个服务:
```properties
raptor.url.com.ppdai.framework.raptor.proto.Simple=http://localhost:8080?connectTimeout=2000&socketTimeout=10000
```
如下参数可以配置在URL上，并被Raptor识别：
| 名称            | 说明           |
| :------------- | :------------- |
| connectionRequestTimeout|从连接池获取连接的超时时间,单位毫秒|
| connectTimeout |客户端和服务器建立连接的超时时间，单位毫秒 |
| socketTimeout   | 客户端从服务器读取数据的超时时间，单位毫秒 |
| accessLog   | 是否开启访问日志 true/false  |
