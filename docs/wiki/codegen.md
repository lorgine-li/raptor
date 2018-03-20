# codegen 详细说明

## 工程生成器
Raptor提供一个生成工程项目的命令行工具`raptor-codegen-cli`。可以在`raptor-codegen-cli`模块中运行`mvn install`命令生成`raptor-codegen-cli-VERSION.jar`。

生成jar包之后通过运行下面的命令生成项目。
```
java -jar raptor-codegen-cli-VERSION.jar -i /path/to/proto/file/ -o /path/to/dest/
```
生成项目中并没有直接生成java类，需要再次在生成的项目目录下运行`mvn install`生成java类。

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
proto2swagger插件和proto2java相似，可以生成swagger json文件。通过swagger json 文件，继而生成其他语言的代码。

