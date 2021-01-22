package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;

@Slf4j
public class CustomEventHandler implements TServerEventHandler {
    @Override
    public void preServe() {
        log.info("preServe: Server 启动");
    }

    @Override
    public ServerContext createContext(TProtocol input, TProtocol output) {
        log.info("createContext: 处理新的请求");
        return null;
    }

    @Override
    public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {
        log.info("deleteContext: 请求处理完成");

    }

    @Override
    public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
        log.info("processContext: 触发调用，处理请求");
    }
}
