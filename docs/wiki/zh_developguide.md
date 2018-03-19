# 如何向Raptor贡献代码
## 概述
Raptor遵循Apache 2.0开源协议，我们欢迎所有人向Raptor项目贡献代码，为开源社区贡献力量。

## 流程

1. （可选）提交Issue，说明你想要增加或修改的功能，我们会与你进行讨论修改的可行性并给出开发的建议。
2. （可选）你也可以认领Issue列表中已有的问题，在Issue中留言告知我们。
3. 开始开发，Raptor的开发流程基于Github flow，请参考[Understanding the GitHub Flow](https://guides.github.com/introduction/flow/)
    1. fork Raptor仓库(详细步骤请参考[Working with forks](https://help.github.com/articles/working-with-forks/))
    2. 在你的仓库中创建新的分支并提交代码。
    3. 创建pull request
    4. 我们会review你的代码并给出修改的建议。
    5. 代码符合要求后，我们会将代码合并到主分支。
4. 我们会把你的名字更新到Raptor的贡献者列表，感谢你对Raptor的贡献！

## 要求

1. 所有Raptor源文件头需要包含Apache 2.0协议：
```
/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
```
2. 代码注释及提交注释使用英文，并使用清晰的描述语句，提交Pull Request前请使用[git rebase](Using Git rebase)功能完善提交记录。
3. 代码格式请参考[《阿里巴巴Java开发手册》](http://techforum-img.cn-hangzhou.oss-pub.aliyun-inc.com/Java_1512024443940.pdf)或已有的源文件，保持风格一致。
4. 开发过程中如果遇到问题可以随时通过Issue与我们联系。

## 如何更新Raptor Wiki
由于github wiki不提供pull request机制，我们在Raptor项目中放置了[wiki目录](http://git.ppdaicorp.com/foundation-framework/raptor/tree/dev/docs/wiki)，你可以提交pull request来修改这些文件，我们会将更新合并到wiki页面中。

# 扩展机制
## 概述
Raptor框架基于扩展机制开发，增加新功能只需按照扩展机制实现对应的接口，甚至可以在不改造Raptor代码本身的基础上增加新的功能。

## 扩展点
* Filter 发送/接收请求过程中增加切面逻辑，默认提供日志统计等功能
* AbstractUrlRepository 通过接口名称获取服务方地址的仓库，提供本地仓库实现和SpringEnv实现
* Client 管理Http Client的逻辑，默认提供ApacheClient实现
* Serialization 扩展序列化方式，默认使用Protocol Buffer序列化
* EndPoint 服务端入口端点，默认使用Servlet实现

## 编写一个Raptor扩展

1. 实现SPI扩展点接口
2. 将实现接口注入到Spring容器中

## 示例插件

### Filter示例
Filter分为ProviderFilter和ReferFilter，他们分别在服务端和客户端生效。在本示例中，我们实现一个ReferFilter。
```java
public class DoNothingFilter implements ReferFilter {

    public static final Logger LOGGER = LoggerFactory.getLogger(DoNothingFilter.class);

    @Override
    public Response filter(Refer<?> refer, Request request) {
        LOGGER.info("I do nothing.");
        return refer.call(request);
    }

    @Override
    public int getOrder() {
        return 500;
    }
}
```

在实现接口之后，我们需要创建一个Bean，并将它注入到Spring容器中。建议使用`@ConditionalOnProperty`等显限定条件的注解注入，方便业务方灵活配置：
```java
@Bean
@ConditionalOnProperty(name = "raptor.refer.filter.doNothing", havingValue = "true", matchIfMissing = true)
public DoNothingFilter createNoNothingFilter() {
    return new DoNothingFilter();
}
```
