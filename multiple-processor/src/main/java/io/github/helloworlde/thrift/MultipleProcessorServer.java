package io.github.helloworlde.thrift;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

@Slf4j
public class MultipleProcessorServer {

    @SneakyThrows
    public static void main(String[] args) {

        HelloServiceImpl helloService = new HelloServiceImpl();
        HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);

        MultipleServiceImpl multipleService = new MultipleServiceImpl();
        MultipleService.Processor<MultipleService.Iface> multipleServiceProcessor = new MultipleService.Processor<>(multipleService);

        TMultiplexedProcessor processor = new TMultiplexedProcessor();
        processor.registerProcessor("HelloService", helloServiceProcessor);
        processor.registerProcessor("MultipleService", multipleServiceProcessor);

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

