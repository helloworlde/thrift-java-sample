package io.github.helloworlde.thrift;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

@Slf4j
public class AsyncServer {

    @SneakyThrows
    public static void main(String[] args) {

        HelloServiceAsyncImpl helloService = new HelloServiceAsyncImpl();
        HelloService.AsyncProcessor<HelloService.AsyncIface> helloServiceProcessor = new HelloService.AsyncProcessor<>(helloService);

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

