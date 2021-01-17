# 使用 Gradle 构建 Thrift 服务

## Gradle 配置

- build.gradle.kts

修改 Gradle 配置，添加 Thrift 依赖，插件

```diff
plugins {
    java
    idea
    application
    id("io.freefair.lombok") version "5.3.0"
+   id("org.jruyi.thrift") version "0.4.1"
}

repositories {
    mavenCentral()
    jcenter()
}

+val thriftVersion = "0.13.0"
val slf4jVersion = "1.7.25"

dependencies {
+   implementation("org.apache.thrift:libthrift:${thriftVersion}")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jVersion}")
}
```

添加完成后，执行构建时会根据 thrift 文件生成相应的 Java 代码

## 添加 thrift

- 在 `src/main/thrift`下添加 thrift 文件

```thrift
namespace java io.github.helloworlde.thrift

struct HelloMessage {
    1: required string message,
}

struct HelloResponse {
    1: required string message,
}

service HelloService {
    HelloResponse sayHello(1: HelloMessage request);
}
```

## 实现 Server 端

- 服务实现

```java
@Slf4j
public class HelloServiceImpl implements HelloService.Iface {

    @Override
    public HelloResponse sayHello(HelloMessage request) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);
        return response;
    }
}
```

- 启动 Server

```java
@Slf4j
public class HelloWorldServer {

    @SneakyThrows
    public static void main(String[] args) {

        HelloServiceImpl helloService = new HelloServiceImpl();
        HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);

        TServerTransport serverTransport = new TServerSocket(9090);

        TServer.Args serverArgs = new TServer.Args(serverTransport)
                .processor(helloServiceProcessor);

        TServer server = new TSimpleServer(serverArgs);
        server.serve();
    }
}
```

## 实现 Client 端

```java
@Slf4j
public class HelloWorldClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Thrift");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

            client.wait(10_000);
        } catch (TException e) {
            e.printStackTrace();
        }

    }
}
```