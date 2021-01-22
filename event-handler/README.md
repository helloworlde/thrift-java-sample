# Thrift 中使用 EventHandler 监听调用事件

Thrift 中的 EventHandler 支持监听服务端的事件，包括服务启动，开始处理请求，执行请求，处理请求完成等事件

## EventHandler 

### 接口定义

```java
public interface TServerEventHandler {

    /**
     * 在 Server 开始调用之前调用
     */
    void preServe();

    /**
     * 当有新的客户端连接，并将要处理时调用
     */
    ServerContext createContext(TProtocol input,
                                TProtocol output);

    /**
     * 当客户端请求处理完成，删除 Server 端 Context
     */
    void deleteContext(ServerContext serverContext,
                       TProtocol input,
                       TProtocol output);

    /**
     * 当客户端开始调用处理器时使用
     */
    void processContext(ServerContext serverContext,
                        TTransport inputTransport,
                        TTransport outputTransport);

}
```

### 自定义实现

```java
@Slf4j
public class CustomEventHandler implements TServerEventHandler {
    @Override
    public void preServe() {
        log.info("preServe: Server 启动");
    }

    @Override
    public ServerContext createContext(TProtocol input, TProtocol output) {
        log.info("createContext: 处理新的请求");
        return null;
    }

    @Override
    public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {
        log.info("deleteContext: 请求处理完成");

    }

    @Override
    public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
        log.info("processContext: 触发调用，处理请求");
    }
}
```

## Server 添加 EventHandler

```diff
@Slf4j
public class EventHandlerServer {

    @SneakyThrows
    public static void main(String[] args) {

        HelloServiceImpl helloService = new HelloServiceImpl();
        HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);

        TNonblockingServerTransport transport = new TNonblockingServerSocket(9090);

        // 配置参数以及处理器
        TThreadedSelectorServer.Args serverArgs = new TThreadedSelectorServer.Args(transport)
                .selectorThreads(4)
                .workerThreads(10)
                .acceptQueueSizePerThread(20)
                .processor(helloServiceProcessor);

        TServer server = new TThreadedSelectorServer(serverArgs);
+       server.setServerEventHandler(new CustomEventHandler());

        server.serve();
    }
}

```

## 测试

启动服务端和客户端，执行请求，可以看到相应日志:

```java
[Thread-4] INFO io.github.helloworlde.thrift.CustomEventHandler - preServe: Server 启动
[Thread-1] INFO io.github.helloworlde.thrift.CustomEventHandler - createContext: 处理新的请求
[pool-1-thread-1] INFO io.github.helloworlde.thrift.CustomEventHandler - processContext: 触发调用，处理请求
[pool-1-thread-1] INFO io.github.helloworlde.thrift.HelloServiceImpl - 接收到请求: CustomEventHandler
[Thread-1] INFO io.github.helloworlde.thrift.CustomEventHandler - deleteContext: 请求处理完成
[Thread-3] INFO io.github.helloworlde.thrift.CustomEventHandler - createContext: 处理新的请求
[pool-1-thread-2] INFO io.github.helloworlde.thrift.CustomEventHandler - processContext: 触发调用，处理请求
[pool-1-thread-2] INFO io.github.helloworlde.thrift.HelloServiceImpl - 接收到请求: CustomEventHandler
[Thread-3] INFO io.github.helloworlde.thrift.CustomEventHandler - deleteContext: 请求处理完成
```