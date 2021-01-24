# Thrift 中使用单端口多服务

Thrift 默认每个 Server 只有一个服务，即每个服务占用一个端口；但是实际使用过程中，这种方式并不可行；会占用大量的端口；Thrift 提供了 `TMultiplexedProcessor`，支持一个端口下有多个服务

## Server 端

Server 端需要将所要提供的服务注册到 `TMultiplexedProcessor` 中，然后向 Server 注册 `TMultiplexedProcessor`；需要客户端在请求时在请求头中添加对应的服务的服务名，从`TMultiplexedProcessor`根据服务名查找相应的处理器进行处理

```diff
@Slf4j
public class MultipleProcessorServer {

    @SneakyThrows
    public static void main(String[] args) {

        HelloServiceImpl helloService = new HelloServiceImpl();
        HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);

        MultipleServiceImpl multipleService = new MultipleServiceImpl();
        MultipleService.Processor<MultipleService.Iface> multipleServiceProcessor = new MultipleService.Processor<>(multipleService);

+       TMultiplexedProcessor processor = new TMultiplexedProcessor();
+       processor.registerProcessor("HelloService", helloServiceProcessor);
+       processor.registerProcessor("MultipleService", multipleServiceProcessor);

        TNonblockingServerTransport transport = new TNonblockingServerSocket(9090);

        // 配置参数以及处理器
        TThreadedSelectorServer.Args serverArgs = new TThreadedSelectorServer.Args(transport)
                .selectorThreads(4)
                .workerThreads(10)
                .acceptQueueSizePerThread(20)
                .processor(processor);

        TServer server = new TThreadedSelectorServer(serverArgs);
        server.serve();
    }
}
```

## Client 端

客户端需要使用 `TMultiplexedProtocol`，这个 Protocol 会在请求头中写入服务名称，用于在 Server 端分割后查找相应的处理方法

```diff
@Slf4j
public class MultipleProcessorClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            TFramedTransport framedTransport = new TFramedTransport(transport);
            framedTransport.open();

            TProtocol protocol = new TBinaryProtocol(framedTransport);

            sayHello(protocol);
            echo(protocol);

        } catch (TException e) {
            e.printStackTrace();
        }

        Thread.sleep(3_000);
    }


    private static void echo(TProtocol protocol) throws TException {
+       protocol = new TMultiplexedProtocol(protocol, "MultipleService");
        MultipleService.Client client = new MultipleService.Client(protocol);

        String response = client.echo("Multiple Processor");
        log.info("返回响应: {}", response);
    }

    private static void sayHello(TProtocol protocol) throws TException {
+       protocol = new TMultiplexedProtocol(protocol, "HelloService");

        HelloService.Client client = new HelloService.Client(protocol);

        HelloMessage request = new HelloMessage();
        request.setMessage("Thrift");

        HelloResponse response = client.sayHello(request);
        log.info("返回响应: {}", response.getMessage());
    }
}

```