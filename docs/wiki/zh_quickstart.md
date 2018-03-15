# Raptor
拍拍贷微服务RPC框架
# 概述
raptor是一套高性能、易于使用的分布式远程服务调用(RPC)框架。

# 功能
- 使用protobuf定义IDL，支持契约优先开发模式。
- 支持通过spring配置方式集成，无需额外编写代码即可为服务提供分布式调用能力。
- 基于高并发、高负载场景进行优化，保障生产环境下RPC服务高可用。

# 快速入门

快速入门中会给出一些基本使用场景下的配置方式，更详细的使用文档请参考

> 如果要执行快速入门介绍中的例子，你需要:
>  * JDK 1.8或更高版本。
>  * java依赖管理工具，如[Maven][maven]。


## 简单调用示例

1. 编写protobuf接口调用的契约IDL，命名为helloworld.proto

    ```proto
    syntax = "proto3";

    package com.ppdai.framework.raptor.proto;
    service Simple {
        rpc sayHello (HelloRequest) returns (HelloReply) {
        }
    }

    message HelloRequest {
        string name = 1;
    }

    message HelloReply {
        string message = 1;
    }

    ```

2. 通过raptor-codegen-cli生成maven工程

    在上一步编写的helloworld.proto文件目录下，执行命令：
    ```cmd
    java -jar raptor-codegen-cli-VERSION.jar -i ./ -o ./
    ```
    执行成功后，会在当前目录下生成一个名为demo-project的maven工程，并将当前目录下所有的proto文件copy到maven工程的src/main/proto。

    可以根据需要修改demo-project pom中的的相关配置。

3. 增加maven依赖

    分别在服务端和客户端的maven工程中添加依赖
    ```xml
       <!--上一步生成的maven工程依赖-->
        <dependency>
            <groupId>com.ppdai.framework</groupId>
            <artifactId>raptor-demo-api</artifactId>
            <version>${raptor-demo-api.version}</version>
        </dependency>
        <!--raptor core 依赖 -->
        <dependency>
            <groupId>com.ppdai.framework</groupId>
            <artifactId>raptor-core</artifactId>
            <version>${raptor.version}</version>
        </dependency>

    ```

4. 服务端实现

    实现Simple接口，该接口由第2步生成的maven工程中插件编译proto文件生成
    ```java
    public class SimpleImpl implements Simple {

        @Override
        public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
            String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt(0, 10000);
            return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
        }

    }
    ```

    采用内置的jetty HttpServer启动服务端
    ```java
    public static void main(String[] args) {
        // 生成servlet
        URL baseUrl = URL.builder()
                .port(8080)
                .path("/raptor")
                .build();
        servletEndpoint = new JettyServletEndpoint(baseUrl);
        servletEndpoint.start();

        //初始化service
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = ProviderBuilder.newBuilder().build(Simple.class, simple);

        servletEndpoint.export(provider);
    }
    ```
5. 客户端实现

    构建客户端代理，rpc调用服务端
    ```java

    public static void main(String[] args) {
        String url = "http://localhost:8080";

        Simple proxy = ReferProxyBuilder.newBuilder().build(Simple.class, URL.valueOf(url));

        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
        System.out.println(helloReply);
    }
    ```

## spring boot 集成示例

1. 参考简单调用示例完成proto文件生成和maven插件生成

2. 增加maven依赖

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

3. 服务端实现

    新建java类SimpleImpl实现api中的接口Simple，并使用注解@RaptorService注册spring bean和发布raptor服务

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

4. 修改服务端spring boot配置

    修改配置文件application.properties，设置启动端口

    ```properties
    server.port=8080
    ```

5. 客户端实现

    在spring bean中使用@RaptorClient注解接口的Field，自动注入代理

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
    客户端构造proto的Message对象，通过接口代理直接调用远程服务

6. 修改客户端端spring boot配置

    修改application.properties文件，修改启动端口，增加Simple接口的url地址配置

    ```properties
    server.port=8081
    raptor.url.com.ppdai.framework.raptor.proto.Simple=http://localhost:8080
    ```
    raptor.url.xxx.yyy 为接口xxx.yyy的url地址

7. 测试验证

    启动服务端客户端spring boot应用，输入地址测试

    ```cmd
    http://127.0.0.1:8081/sayhello?name=test
    ```
    
