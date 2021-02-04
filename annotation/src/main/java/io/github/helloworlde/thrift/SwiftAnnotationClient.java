package io.github.helloworlde.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class SwiftAnnotationClient {

    public static void main(String[] args) {

        try {
            ThriftClientManager clientManager = new ThriftClientManager();
            FramedClientConnector clientConnector = new FramedClientConnector(new InetSocketAddress("localhost", 9090));
            HelloService helloService = clientManager.createClient(clientConnector, HelloService.class).get();

            HelloRequest request = new HelloRequest();
            request.setMessage("Swift Annotation");

            HelloResponse response = helloService.sayHello(request);
            log.info("返回响应, message: {}, details: {}", response.getMessage(), response.getDetails());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
