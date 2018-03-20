
## 配置概述
Raptor通常和Spring Boot配套使用。子项目raptor-springboot基于spring-boot提供自动配置、自动装配能力进行组装，使raptor框架开箱即用。


### 服务端地址
需要在客户端配置各个服务端地址。在`application.properties`中加上如下配置：
```properties
raptor.url.xxx.xxx.XxxApi=http://host:port
```
其中`xxx.xxx.XxxApi`为服务接口的完全限定类名(Fully-Qualified Class Name,FQCN)

> 默认前缀为`raptor.url.`,可以通过`raptor.url.prefix`属性自定义前缀。


### 配置URL参数
服务的超时时间可以通过URL参数配在Url上,例如：

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
