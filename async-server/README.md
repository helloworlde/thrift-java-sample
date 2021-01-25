# Thrift 中使用服务端异步处理

参考 [Nonblocking Server](../nonblocking-server), 使用非阻塞的异步的 Server

## Server 端

Server 端提供了 `AsyncProcessor` 支持请求的异步处理；与同步处理的不同的是会在方法中传入 `AsyncMethodCallback`，用于发送响应

- 服务实现

服务实现使用 `AsyncIface` 代替了 `Iface`

```diff
@Slf4j
-public class HelloServiceImpl implements HelloService.Iface {
+public class HelloServiceAsyncImpl implements HelloService.AsyncIface {

    @Override
-   public HelloResponse sayHello(HelloMessage request) throws TException {    
+   public void sayHello(HelloMessage request, AsyncMethodCallback<HelloResponse> resultHandler) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);

-       return response;
+       resultHandler.onComplete(response);
    }
}
```

- 使用非阻塞的 Server

```diff
@Slf4j
public class AsyncServer {

    @SneakyThrows
    public static void main(String[] args) {


-       HelloServiceImpl helloService = new HelloServiceImpl();
-       HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);
+       HelloServiceAsyncImpl helloService = new HelloServiceAsyncImpl();
+       HelloService.AsyncProcessor<HelloService.AsyncIface> helloServiceProcessor = new HelloService.AsyncProcessor<>(helloService);

        TNonblockingServerTransport transport = new TNonblockingServerSocket(9090);

        // 配置参数以及处理器
        TThreadedSelectorServer.Args serverArgs = new TThreadedSelectorServer.Args(transport)
                .selectorThreads(4)
                .workerThreads(10)
                .acceptQueueSizePerThread(20)
                .processor(helloServiceProcessor);

        TServer server = new TThreadedSelectorServer(serverArgs);
        server.serve();
    }
}
```

## Client 端

客户端使用阻塞的 `TSocket`，因为 Server 端需要使用 `TFramedTransport`，所以无法使用非阻塞的客户端 

```diff
@Slf4j
public class BlockingClient {

    public static void main(String[] args) {

        try {
            TTransport transport = new TSocket("localhost", 9090);
+           transport = new TFramedTransport(transport);

            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Async Server");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
```