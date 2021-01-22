package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

@Slf4j
public class EventHandlerClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            TFramedTransport framedTransport = new TFramedTransport(transport);
            framedTransport.open();

            TProtocol protocol = new TBinaryProtocol(framedTransport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("CustomEventHandler");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
