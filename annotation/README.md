# Thrift 中使用注解开发服务

Facebook 开源的 Swift，支持使用注解的方式生成 Thrift 相关类，而无需 IDL 文件；同时也提供了 Jar 和 Maven 插件，可以通过注解类生成 Thrift IDL 文件
该项目已经废弃，可以使用 [https://github.com/airlift/drift](https://github.com/airlift/drift) 代替

## Swift 注解

Swift 中的注解用途和 IDL 中的注解一致；依赖是 `com.facebook.swift:swift-annotations`

- ThriftConstructor: 标记实体类的构造方法
- ThriftEnum: 标记枚举类
- ThriftEnumValue: 标记枚举类属性
- ThriftField: 标记实体类属性
- ThriftIdlAnnotation: 用于 `ThriftField`，`ThriftUnionId` 等，用于属性的标记
- ThriftStruct: 标记实体类
- ThriftUnion: 标记关联实体类
- ThriftUnionId: 标记关联实体属性
- ThriftService: 标记服务
- ThriftMethod: 标记服务方法
- ThriftException: 声明方法异常

## 用注解定义

- 服务

```java
@ThriftService
public interface HelloService {

    @ThriftMethod
    HelloResponse sayHello(HelloRequest request);
}
```

- 实体类

```java
@Data
@ThriftStruct
public class HelloResponse {

    @ThriftConstructor
    public HelloResponse() {
    }

    @ThriftField(1)
    public String message;

    @ThriftField(2)
    public List<String> details;
}
```

## 服务端和客户端

- 服务端

服务端底层实现使用的是基于 Netty 实现的 Nifty；主要的方法调用在 `ThriftMethodProcessor` 中异步实现

```java
    @SneakyThrows
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(),
                Collections.singletonList(new CustomThriftEventHandler()),
                helloService);

        ThriftServer server = new ThriftServer(processor, new ThriftServerConfig().setPort(9090));
        server.start();
    }
```

- 客户端

客户端同样是使用 Nifty 实现调用

```java
    public static void main(String[] args) {

        try {
            ThriftClientManager clientManager = new ThriftClientManager();
            FramedClientConnector clientConnector = new FramedClientConnector(new InetSocketAddress("localhost", 9090));
            HelloService helloService = clientManager.createClient(clientConnector, HelloService.class).get();

            HelloRequest request = new HelloRequest();
            request.setMessage("Swift Annotation");

            HelloResponse response = helloService.sayHello(request);
            log.info("返回响应, message: {}, details: {}", response.getMessage(), response.getDetails());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```