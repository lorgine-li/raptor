

todo TOC


# 基本介绍
Raptor是一套高性能、易于使用的分布式远程服务调用(RPC)框架。Raptor具有良好的扩展性，主要模块都支持二次开发，可以方便的扩展。Raptor拥抱契约优先的开发方式，支持生成多语言的客户端和服务端。

## 架构概述
Raptor中分为服务提供方(RPC Server)，服务调用方(RPC Client)两个角色，以及codegen这个主要插件。


* raptor-codegen 通过 `.proto` 文件生成接口文件；
* Server通过生成的接口的实现类自动生成Servlet，供Client调用；
* Client通过生成的接口自动生成调用代理类，通过代理类直接调用Server提供的服务。


三者的交互关系如下图：
![](assets/zh_userguide-7f7ca752.png)


## 模块概述

Raptor框架中主要有client、refer、filter、serialization、provider、endpoint 几个功能模块，各个功能模块都支持通过SPI进行扩展，各模块的交互如下图所示：


![](assets/zh_userguide-44fdce8c.png)


#### filter
filter在调用refer和provider之前调用，可以完成统计、并发控制等功能。

#### refer
持有服务提供方暴露的interface和服务的url，通过调用client和server进行通信。在平常用户使用的时候，refer通常被包装在InvokeHandler中使用。

#### serialize
将RPC请求中的参数、结果等对象进行序列化与反序列化，即进行对象与字节流的互相转换；默认使用 protobuf协议。

#### client
Http客户端，通过设置 `serialization` 可以选择 protobuf binary over HTTP 或者 protobuf json over HTTP。

#### provider
提供具体服务实现类的具体方法（Method 对象），供外部调用者调用。

#### endpoint
维护多个provider，负责将provider暴露给web容器。默认实现是endpoint同时作为Servlet，以暴露provider。



## 配置概述
Raptor通常和Spring Boot配套使用。子项目raptor-springboot基于spring-boot提供自动配置、自动装配能力进行组装，使raptor形成框架开箱即用。


### 服务端地址
需要在客户端配置各个服务端地址。在`application.properties`中加上如下配置：
```properties
raptor.url.xxx.xxx.XxxApi=http://host:port/
```
其中`xxx.xxx.XxxApi`为服务接口的完全限定类名(Fully-Qualified Class Name,FQCN)

> 默认前缀为`raptor.url.`,可以通过`raptor.url.prefix`属性自定义前缀。


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





# 使用Raptor
Raptor主要和Spring Boot集成，基本做到零配置，可插拔。关于在项目中使用Raptor框架的具体步骤，请参考：[快速入门](http://git.ppdaicorp.com/foundation-framework/raptor/blob/master/README.md)。


## 处理调用异常
Raptor框架定义了三种异常`RaptorBizException`,`RaptorFrameworkException`和`RaptorServiceException`。他们都继承了`RaptorAbstractException`。

* RaptorBizException
`RaptorBizException`代表服务端发生业务异常。
* RaptorServiceException
`RaptorServiceException`代表服务调用异常，如方法未找到、服务端抛出Error等。
* RaptorFrameworkException
`RaptorFrameworkException`代表框架内部异常。


# 常用功能介绍
## mock测试
Raptor提供MockClient进行客户端的mock。

### 使用MockClinet进行mock
使用MockClinet的方法为构造MockClinet，声明为spring bean：
```java
@Bean
public Client createMockClient(){
    byte[] mockData = new byte[0];
    MockClient mockClient = MockClient.builder()
            .content(mockData).build();
    return mockClient;
}
```
其中`mockData`为自定义的byte数组，这里用空数组，也可以通过`serialization`来构造`mockData`。


# 运维及监控

## 日志说明
Raptor会打印访问日志。服务端和客户端的日志格式一致，如下所示：

```
com.ppdai.framework.raptor.filter.AbstractAccessLogFilter- refer/server|client host|server host|interface|method|version|raptor Version|appId|requestId|status code|requestTime
```
各个属性含义如下：
* refer/server -- `refer`代表客户端，`server`代表服务端
* client host --  该请求客户端主机IP
* server host -- 该请求服务端端主机IP
* interface -- 该请求的完全限定类名
* method -- 请求方法（interface 定义的方法）
* version -- 接口版本
* raptor Version -- 框架版本
* appId -- 应用ID
* requestId -- 请求ID
* status code -- 返回结果状态值
* requestTime -- 相应时间

当值不存在时，以空字符串代替。



### Metric 统计
Raptor默认启用[Metrics](http://metrics.dropwizard.io/4.0.0/)统计。
主要统各个服务的并发数，相应时间，调用次数。

todo 问轶丛tag 具体能干啥


# 性能测试

# codegen 详细说明

## 工程生成器
Raptor提供一个生成工程项目的命令行工具`raptor-codegen-cli`。可以在`raptor-codegen-cli`模块中运行`mvn install`命令生成`raptor-codegen-cli-VERSION.jar`。

生成jar包之后通过运行下面的命令生成项目。
```
java -jar raptor-codegen-cli-VERSION.jar -i /path/to/proto/file/ -o /path/to/dest/
```
生成项目中并没有直接生成java类，需要再次在项目目录下运行`mvn install`生成java类。

## proto2java Maven插件
通过如下配置使用proto2java插件。
```xml
<plugins>
    <plugin>
        <groupId>com.ppdai.framework</groupId>
        <artifactId>raptor-codegen-maven</artifactId>
        <version>${raptor.version}</version>
        <executions>
            <execution>
                <goals>
                    <goal>proto2java</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```
插件需要使用到protoc(The protocol compiler),protoc默认是从[Maven主库](http://repo1.maven.org/maven2)下载,可以通过`<releaseUrl>`属性进行配置。

插件默认从`${project.directory}/src/main/proto/`目录扫描proto文件。可以通过`<inputDirectories>`属性更改或添加扫描输入文件夹。

生成的Java文件默认放在`${project.build.directory}/generated-sources`中，可以通过`<outputDirectory>`属性配置。

## proto2swagger Maven插件
proto2swagger插件和proto2java相似。


# 其他

## RequestId 生成算法
RequestId共64位，位置分配如下：
* 1bit   符号位
* 41bits 时间偏移量从2016年11月1日零点到现在的毫秒数
* 10bits workId
* 12bits 同一个毫秒内的自增量

其中workId算法为服务器ip二进制最后十位，每毫秒自增量取值范围为[0,4095],如果每毫秒自增量超过4095，就会自动延迟到下一毫秒从0计算。
