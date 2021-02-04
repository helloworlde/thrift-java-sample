package io.github.helloworlde.thrift;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public HelloResponse sayHello(HelloRequest request) {
        log.info("请求: {}", request.getMessage());
        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + request.getMessage());
        response.setDetails(Lists.newArrayList("1", "2"));
        return response;
    }
}
