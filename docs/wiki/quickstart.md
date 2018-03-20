# raptor快速入门

要执行快速入门介绍中的例子，需要：
>  * JDK 1.8或更高版本。
>  * java依赖管理工具`maven`。


1. [编写protobuf接口](#编写protobuf接口)
2. [获取代码生成器](#获取代码生成器)
3. [codegen生成代码](#codegen生成代码)
4. [新建客户端/服务端工程](#新建客户端/服务端工程)
5. [服务端实现](#服务端实现)
6. [客户端实现](#客户端实现)


## 编写protobuf接口

编写protobuf的契约文件，命名为helloworld.proto
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
## 获取代码生成器
在maven仓库中下载代码生成器工具[raptor-codegen-cli-VERSION.jar]()

或者通过源代码build，在源代码目录执行maven build命令：
```cmd
mvn clean package
```
在raptor/raptor-codegen/raptor-codegen-cli/target目录下得到raptor-codegen-cli-VERSION.jar

## codegen生成代码

执行命令生成maven工程：
```cmd
java -jar raptor-codegen-cli-VERSION.jar -i ./ -o ./
```

执行成功后，会在当前目录下生成一个名为demo-project的maven工程，并将当前目录下所有的proto文件copy到maven工程的src/main/proto，也可以手工将编写好的proto文件copy到该maven工程的src/main/proto下。

根据需要修改demo-project，包括工程名称、pom中的的相关配置。

build生成的maven工程：
```
maven clean compile
```
构建后会将src/main/proto下的所有proto文件生成java代码到target/generated-sources

## 新建客户端/服务端工程

新建客户端/服务端的maven工程，并添加依赖
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

## 服务端实现

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

## 客户端实现

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
