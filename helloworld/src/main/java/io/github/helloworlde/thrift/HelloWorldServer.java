package io.github.helloworlde.thrift;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

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

