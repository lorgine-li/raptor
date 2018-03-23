# 运维及监控

## 日志说明
Raptor会打印访问日志。服务端和客户端的日志格式一致，如下所示：

```
com.ppdai.framework.raptor.filter.AbstractAccessLogFilter- refer/server|client host|server host|interface|method|version|raptor Version|appId|requestId|status code|requestTime
```
各个属性含义如下：
* refer/server -- `refer`代表客户端，`server`代表服务端
* client host --  该请求客户端主机IP
* server host -- 该请求服务端主机IP
* interface -- 该请求的完全限定类名
* method -- 请求方法（interface 定义的方法）
* version -- 接口版本
* raptor Version -- raptor框架版本
* appId -- 应用ID
* requestId -- 请求ID
* status code -- 返回结果状态值
* requestTime -- 响应时间

当值不存在时，以空字符串代替。



### Metric 统计
Raptor默认启用[Metrics](http://metrics.dropwizard.io/4.0.0/)统计。
主要统各个服务的并发数，相应时间，调用次数。
