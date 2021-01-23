package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/hello")
@Slf4j
public class HelloServiceServlet extends TServlet {

    public HelloServiceServlet() {
        // 这里的协议要和 Client 端一致
        super(new HelloService.Processor<>(new HelloServiceImpl()), new TJSONProtocol.Factory());
    }
}

@Slf4j
class HelloServiceImpl implements HelloService.Iface {

    @Override
    public HelloResponse sayHello(HelloMessage request) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);
        return response;
    }
}