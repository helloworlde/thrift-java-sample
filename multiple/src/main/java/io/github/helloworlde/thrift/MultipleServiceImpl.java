package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
public class MultipleServiceImpl implements MultipleService.Iface {

    @Override
    public String echo(String message) throws TException {
        log.info("接收到请求: {}", message);
        return message;
    }
}
