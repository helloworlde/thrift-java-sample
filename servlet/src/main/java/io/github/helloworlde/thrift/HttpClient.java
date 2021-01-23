package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;

@Slf4j
public class HttpClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            THttpClient transport = new THttpClient("http://localhost:8080/hello");
            TProtocol protocol = new TJSONProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Servlet");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

        } catch (TException e) {
            e.printStackTrace();
        }

        Thread.sleep(3_000);
    }
}
