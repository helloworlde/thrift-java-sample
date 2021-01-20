# Thrift 中使用 Oneway

oneway 是 Thrift 提供的一种调用方式，客户端只发送请求而不等待响应；适用于记录日志等场景

## 修改 thrift

- 修改 thrift 文件

使用 oneway 关键字修饰方法

```diff
namespace java io.github.helloworlde.thrift

struct HelloMessage {
    1: required string message,
}

-struct HelloResponse {
-    1: required string message,
-}

service HelloService {
-   HelloResponse sayHello(1: HelloMessage request);
+   oneway void sayHello(1: HelloMessage request);
}
```

## 实现 Server 端

- 服务实现

修改 Server 端实现

```diff
@Slf4j
public class HelloServiceImpl implements HelloService.Iface {

    @Override
    public HelloResponse sayHello(HelloMessage request) throws TException {
    public void sayHello(HelloMessage request) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

-       HelloResponse response = new HelloResponse();
-       response.setMessage("Hello " + message);
-       return response;
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

```diff
@Slf4j
public class HelloWorldClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Oneway");
-           HelloResponse response = client.sayHello(request);
+           client.sayHello(request);
-           log.info("返回响应: {}", response.getMessage());
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
```