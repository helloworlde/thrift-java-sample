package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
public class HelloServiceImpl implements HelloService.Iface {

    @Override
    public HelloResponse sayHello(HelloMessage request) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);
        return response;
    }
}
