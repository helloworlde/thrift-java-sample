package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;

@Slf4j
public class SslClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TSSLTransportFactory.TSSLTransportParameters transportParameters = new TSSLTransportFactory.TSSLTransportParameters();
            transportParameters.setTrustStore("ssl/src/main/resources/certs/public.jks", "123456", "SunX509", "JKS");
            TSocket clientSocket = TSSLTransportFactory.getClientSocket("localhost", 9090, 0, transportParameters);
            TProtocol protocol = new TBinaryProtocol(clientSocket);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("SSL Thrift");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
