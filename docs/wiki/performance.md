
# 性能测试

### 测试环境

#### 硬件配置

     Server端：
     虚拟机配置： 	4C8G
     系统：	CentOS release 6.7 (Final)

     Client端：
     虚拟机配置： 	4C8G
     系统：	CentOS release 6.7 (Final)

#### 软件配置
     JDK版本：1.8

     JVM参数：
     -Xms2048m -Xmx2048m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC

### 测试脚本

#### Server测试场景：

单个Client，线程从5递增到100（step 5），测试Server极限性能。

#### Client测试场景：
1. 在客户端使用`MockClient`模拟发送HTTP请求,实际不发请求。每次调用时返回准备好的二进制数据。这样子测试，测试到了请求在Client所经历的时间，包括序列化，反序列化，Filter等用时。对比二进制序列化和Json序列化。
2. 使用`Serialization`直接对数据序列化和反序列化。这样只测试了序列化时间和反序列化时间。

### 测试结果

#### Server测试结果：

    请求1KString：单Server极限TPS：3832
    请求5KString：单Server极限TPS：2816
    请求10KString：单Server极限TPS：1921

#### Client测试结果：


| 测试项目    | 吞吐量 （ops/ms） | 执行时间（ms）|
| :------------- | :------------- | :------------- |
|二进制序列化    |  1216.337 ± 519.182 |0.001 ±   0.001 |
|Json序列化   |   163.101 ±  30.792 |0.006 ±   0.002 |
|使用二进制序列化发送请求|    41.388 ±   7.153 |0.028 ±   0.013 |
|使用Json序列化发送请求 |    29.864 ±  10.928 |0.034 ±   0.014 |
