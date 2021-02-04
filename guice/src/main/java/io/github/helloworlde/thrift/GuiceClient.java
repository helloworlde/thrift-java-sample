package io.github.helloworlde.thrift;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.codec.guice.ThriftCodecModule;
import com.facebook.swift.service.ThriftClient;
import com.facebook.swift.service.guice.ThriftClientBinder;
import com.facebook.swift.service.guice.ThriftClientModule;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import io.airlift.configuration.ConfigurationFactory;
import io.airlift.configuration.ConfigurationModule;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class GuiceClient {

    public static void main(String[] args) {

        try {
            Injector injector = Guice.createInjector(
                    Stage.PRODUCTION,
                    new ConfigurationModule(new ConfigurationFactory(ImmutableMap.of())),
                    new ThriftCodecModule(),
                    new ThriftClientModule(),
                    binder -> ThriftClientBinder.thriftClientBinder(binder)
                                                .bindThriftClient(HelloService.class)
            );

            FramedClientConnector clientConnector = new FramedClientConnector(new InetSocketAddress("localhost", 61715));
            ThriftClient<HelloService> client = injector.getInstance(Key.get(new TypeLiteral<ThriftClient<HelloService>>() {
            }));

            HelloService helloService = client.open(clientConnector).get();

            HelloRequest request = new HelloRequest();
            request.setMessage("Guice Client");

            HelloResponse response = helloService.sayHello(request);
            log.info("返回响应, message: {}, details: {}", response.getMessage(), response.getDetails());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

