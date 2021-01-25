package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

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
        protocol = new TMultiplexedProtocol(protocol, "MultipleService");
        MultipleService.Client client = new MultipleService.Client(protocol);

        String response = client.echo("Multiple Processor");
        log.info("返回响应: {}", response);
    }

    private static void sayHello(TProtocol protocol) throws TException {
        protocol = new TMultiplexedProtocol(protocol, "HelloService");

        HelloService.Client client = new HelloService.Client(protocol);

        HelloMessage request = new HelloMessage();
        request.setMessage("Thrift");

        HelloResponse response = client.sayHello(request);
        log.info("返回响应: {}", response.getMessage());
    }
}
