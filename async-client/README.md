# Thrift 中使用异步的 Client

参考 [Nonblocking Server](../nonblocking-server), 使用异步的 Client

## Server 端

- 使用非阻塞的 Server

```diff
@Slf4j
public class NonblockingServer {

    @SneakyThrows
    public static void main(String[] args) {

        HelloServiceImpl helloService = new HelloServiceImpl();
        HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);
        
-       TServerTransport serverTransport = new TServerSocket(9090);
+       TNonblockingServerTransport transport = new TNonblockingServerSocket(9090);

-       TServer.Args serverArgs = new TServer.Args(serverTransport)
-               .processor(helloServiceProcessor);

        // 配置参数以及处理器
+       TThreadedSelectorServer.Args serverArgs = new TThreadedSelectorServer.Args(transport)
+               .selectorThreads(4)
+               .workerThreads(10)
+               .acceptQueueSizePerThread(20)
+               .processor(helloServiceProcessor);

-       TServer server = new TSimpleServer(serverArgs);
+       TServer server = new TThreadedSelectorServer(serverArgs);
        
        server.serve();
    }
}
```

## Client 端

客户端使用 `TNonblockingSocket`，响应结果通过异步回调触发

```java
@Slf4j
public class AsyncClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            // 构建异步客户端
            TAsyncClientManager clientManager = new TAsyncClientManager();
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

            HelloService.AsyncClient.Factory factory = new HelloService.AsyncClient.Factory(clientManager, protocolFactory);

            TNonblockingTransport nonblockingTransport = new TNonblockingSocket("localhost", 9090);
            HelloService.AsyncClient asyncClient = factory.getAsyncClient(nonblockingTransport);

            // 异步回调
            AsyncMethodCallback<HelloResponse> callback = new AsyncMethodCallback<HelloResponse>() {
                @Override
                public void onComplete(HelloResponse response) {
                    log.info("响应结果: {}", response.getMessage());
                }

                @Override
                public void onError(Exception exception) {
                    log.error("请求失败: {}", exception.getMessage(), exception);
                }
            };

            // 构建请求
            HelloMessage request = new HelloMessage();
            request.setMessage("Async Thrift");

            // 调用
            asyncClient.sayHello(request, callback);

        } catch (TException | IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(3_000);
    }
}
```