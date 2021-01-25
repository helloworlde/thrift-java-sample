package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

@Slf4j
public class HelloServiceAsyncImpl implements HelloService.AsyncIface {

    @Override
    public void sayHello(HelloMessage request, AsyncMethodCallback<HelloResponse> resultHandler) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);

        resultHandler.onComplete(response);
    }
}
