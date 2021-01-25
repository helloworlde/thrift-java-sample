package io.github.helloworlde.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

import java.io.IOException;

@Slf4j
public class AsyncClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            // 构建异步客户端
            TAsyncClientManager clientManager = new TAsyncClientManager();
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

            HelloService.AsyncClient.Factory factory = new HelloService.AsyncClient.Factory(clientManager, protocolFactory);

            TNonblockingTransport nonblockingTransport = new TNonblockingSocket("localhost", 9090);
            HelloService.AsyncClient asyncClient = factory.getAsyncClient(nonblockingTransport);

            // 异步回调
            AsyncMethodCallback<HelloResponse> callback = new AsyncMethodCallback<HelloResponse>() {
                @Override
                public void onComplete(HelloResponse response) {
                    log.info("响应结果: {}", response.getMessage());
                }

                @Override
                public void onError(Exception exception) {
                    log.error("请求失败: {}", exception.getMessage(), exception);
                }
            };

            // 构建请求
            HelloMessage request = new HelloMessage();
            request.setMessage("Async Thrift");

            // 调用
            asyncClient.sayHello(request, callback);

        } catch (TException | IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(3_000);
    }
}
