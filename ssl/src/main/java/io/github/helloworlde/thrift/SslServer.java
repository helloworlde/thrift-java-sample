package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;

@Slf4j
public class SslServer {

    public static void main(String[] args) {

        try {
            HelloServiceImpl helloService = new HelloServiceImpl();
            HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);

            TSSLTransportFactory.TSSLTransportParameters transportParameters = new TSSLTransportFactory.TSSLTransportParameters();
            transportParameters.setKeyStore("ssl/src/main/resources/certs/server.jks", "123456");

            TServerSocket serverSocket = TSSLTransportFactory.getServerSocket(9090, 0, null, transportParameters);

            TServer.Args serverArgs = new TServer.Args(serverSocket);
            serverArgs.processor(helloServiceProcessor);

            TSimpleServer server = new TSimpleServer(serverArgs);
            log.info("Server startup");
            server.serve();

        } catch (Exception e) {
            log.error("启动 Server 失败: {}", e.getMessage(), e);
        }
    }
}

