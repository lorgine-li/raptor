# spring boot 集成示例

1. [编写proto文件和生成maven工程](#编写proto文件和生成maven工程)
2. [新建springboot客户端/服务端maven工程](#新建springboot客户端/服务端maven工程)
3. [服务端实现](#服务端实现)
4. [服务端springboot配置](#服务端springboot配置)
5. [客户端实现](#客户端实现)
6. [客户端端springboot配置](#客户端端springboot配置)
7. [测试验证](#测试验证)

## 编写proto文件和生成maven工程
参考 [简单调用示例](quickstart) 完成proto文件生成和maven工程生成

## 新建springboot客户端/服务端maven工程

分别在服务端和客户端的spring boot maven工程中添加依赖
```xml
   <!--上一步生成的maven工程依赖-->
    <dependency>
        <groupId>com.ppdai.framework</groupId>
        <artifactId>raptor-demo-api</artifactId>
        <version>${raptor-demo-api.version}</version>
    </dependency>
    <!--raptor spring boot 依赖 -->
    <dependency>
        <groupId>com.ppdai.framework</groupId>
        <artifactId>raptor-springboot</artifactId>
        <version>${raptor.version}</version>
    </dependency>

```

## 服务端实现

新建api工程中的接口Simple的实现类SimpleImpl，使用注解@RaptorService将SimpleImpl注册为spring bean和发布raptor服务

```java
@RaptorService
public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
```

## 服务端springboot配置

修改配置文件application.properties，设置启动端口

```properties
server.port=8080
```

## 客户端实现

在spring bean中使用@RaptorClient注解接口的Field，自动注入代理，使用接口代理的方法进行rpc call

```java

@RestController
public class SayHelloController {

    @raptorClient
    private Simple simple;

    @RequestMapping("/sayhello")
    public Object sayHello(@RequestParam("name") String name) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName(name).build();
        Helloworld.HelloReply helloReply = simple.sayHello(helloRequest);
        return helloReply != null ? helloReply.toString() : "null";
    }
}

```

## 客户端端springboot配置

修改application.properties文件，修改启动端口，增加Simple接口的url地址配置

```properties
server.port=8081
raptor.url.com.ppdai.framework.raptor.proto.Simple=http://localhost:8080
```
raptor.url.com.ppdai.framework.raptor.proto.Simple 为接口com.ppdai.framework.raptor.proto.Simple的url地址

## 测试验证

启动服务端客户端spring boot应用，输入地址测试

```cmd
http://127.0.0.1:8081/sayhello?name=test
```
