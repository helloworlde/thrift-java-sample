# Thrift 中使用 Guice 框架

[Guice](https://github.com/google/guice) 框架是 Google 开源的支持依赖注入的轻量级框架

使用 Swift 注解的方式，底层通讯使用 Nifty，通过 Guice 注入依赖，实现 Thrift 客户端和服务端

## 服务端

```java
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
```

## 客户端

```java
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
```

