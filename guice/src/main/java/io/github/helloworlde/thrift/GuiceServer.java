package io.github.helloworlde.thrift;

import com.facebook.swift.codec.guice.ThriftCodecModule;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.guice.ThriftServerModule;
import com.facebook.swift.service.guice.ThriftServiceExporter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import io.airlift.configuration.ConfigurationFactory;
import io.airlift.configuration.ConfigurationModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiceServer {

    @SneakyThrows
    public static void main(String[] args) {

        Injector injector = Guice.createInjector(
                Stage.PRODUCTION,
                new ConfigurationModule(new ConfigurationFactory(ImmutableMap.of())),
                new ThriftCodecModule(),
                new ThriftServerModule(),
                binder -> {
                    binder.bind(HelloServiceImpl.class).in(Scopes.SINGLETON);
                    ThriftServiceExporter.thriftServerBinder(binder)
                                         .exportThriftService(HelloServiceImpl.class);
                    ThriftServiceExporter.thriftServerBinder(binder)
                                         .addEventHandler(new CustomThriftEventHandler());

                }
        );

        ThriftServer server = injector.getInstance(ThriftServer.class);
        server.start();
    }
}

