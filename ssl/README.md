# Thrift 中使用 SSL 加密

Thrift 支持使用 SSL 对链路加密

## 生成证书

分别生成公钥和私钥，需要设置信息和密码

```shell
keytool -genkeypair -alias thrift -keyalg RSA -validity 3650 -keystore server.jks
keytool -export -alias thrift -keystore server.jks -rfc -file public.cer
keytool -import -alias thrift -file public.cer  -keystore public.jks
```

## Server 端

无论是 Server 端还是 Client 端，都需要使用 `TSSLTransportParameters` 构建底层的 Transport

Server 端需要指定私钥

```diff
@Slf4j
public class SslServer {

    public static void main(String[] args) {

        try {
            HelloServiceImpl helloService = new HelloServiceImpl();
            HelloService.Processor<HelloService.Iface> helloServiceProcessor = new HelloService.Processor<>(helloService);

+           TSSLTransportFactory.TSSLTransportParameters transportParameters = new TSSLTransportFactory.TSSLTransportParameters();
+           transportParameters.setKeyStore("ssl/src/main/resources/certs/server.jks", "123456");
            
-           TServerTransport serverSocket = new TServerSocket(9090);
+           TServerSocket serverSocket = TSSLTransportFactory.getServerSocket(9090, 0, null, transportParameters);

            TServer.Args serverArgs = new TServer.Args(serverSocket);
            serverArgs.processor(helloServiceProcessor);

            TSimpleServer server = new TSimpleServer(serverArgs);
            log.info("Server startup");
            server.serve();

        } catch (Exception e) {
            log.error("启动 Server 失败: {}", e.getMessage(), e);
        }
    }
}
```

## Client 端

Client 端需要指定公钥

```diff
@Slf4j
public class HelloWorldClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Thrift");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

            client.wait(10_000);
        } catch (TException e) {
            e.printStackTrace();
        }

    }
}
@Slf4j
public class SslClient {

    public static void main(String[] args) throws InterruptedException {

        try {
-           TTransport transport = new TSocket("localhost", 9090);
-           transport.open();
-           TProtocol protocol = new TBinaryProtocol(transport);
                    
+           TSSLTransportFactory.TSSLTransportParameters transportParameters = new TSSLTransportFactory.TSSLTransportParameters();
+           transportParameters.setTrustStore("ssl/src/main/resources/certs/public.jks", "123456", "SunX509", "JKS");
+           TSocket clientSocket = TSSLTransportFactory.getClientSocket("localhost", 9090, 0, transportParameters);
+           TProtocol protocol = new TBinaryProtocol(clientSocket);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("SSL Thrift");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
```

## 参考文档

- [Test Keys and Certificates](https://thrift.apache.org/test/keys)