package io.github.helloworlde.thrift;

import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServerConfig;
import com.facebook.swift.service.ThriftServiceProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class SwiftAnnotationServer {

    @SneakyThrows
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(),
                Collections.singletonList(new CustomThriftEventHandler()),
                helloService);

        ThriftServer server = new ThriftServer(processor, new ThriftServerConfig().setPort(9090));
        server.start();
    }
}

