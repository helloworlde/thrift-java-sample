# Thrift 中使用非阻塞调用

参考 [helloworld](helloworld), 使用非阻塞的 Client 和 Server

## 实现 Server 端

- 使用非阻塞的 Server

Thrift 中有多种类型的 Server，其中非阻塞的有 `THsHaServer` 和 `TThreadSelectorServer` 两种实现；其中 `TThreadSelectorServer` 支持配置 Selector 和 Worker 的线程数量及队列大小，底层使用 NIO

Server 端使用 `TNonblockingServerTransport` 替换了 `TServerTransport`；使用 `TThreadedSelectorServer` 替换了 `TSimpleServer`

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

## 实现 Client 端

客户端的区别是使用 `TFramedTransport` 将 `TSocket` 封装了一层

```diff
@Slf4j
public class NonblockingClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TTransport transport  = new TSocket("localhost", 9090);
+           TFramedTransport framedTransport = new TFramedTransport(transport);
            framedTransport.open();

            TProtocol protocol = new TBinaryProtocol(framedTransport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Nonblocking Thrift");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
```