# Raptor的序列化方式
Raptor可以处理Protobuf或者Json编码的请求和响应.

序列化方式对具体的业务实现是透明的.Raptor将HTTP请求转化成接口中定义的数据结构,并将接口返回的数据结构序列化到HTTP响应中.

Raptor有两种序列化实现:
`ProtobufBinSerialization`:处理Protobuf格式的序列化和反序列化
`ProtobufJsonSerialization`:处理Json格式的序列化和反序列化

## 应该选择哪种序列化方式?
大多数情况下应该使用`ProtobufBinSerialization`.相比于Json,Portobuf序列化更快,序列化后数据更小.
另外,Protobuf被设计成可以优雅地对数据字段进行更新.可以看到`.proto`文件中的定义,每个字段后都跟着`tag number`,这使得在我们修改Protobuf定义之后,任然可以在不同版本的Server和Clinet中继续使用这个数据结构.

## 如果Protobuf更好,为什么要提供Json序列化方式?
在Client中,你几乎用不到`ProtobufJsonSerialization`,但让你的Server能自动处理Json格式的请求还是很方便的.开发测试调试阶段，可以用Json，开发更友好；我们暂不支持的语言客户端，需要访问服务，可以调用json接口，自己解析Json.
