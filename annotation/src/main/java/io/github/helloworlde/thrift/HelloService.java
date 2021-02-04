package io.github.helloworlde.thrift;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

@ThriftService
public interface HelloService {

    @ThriftMethod
    HelloResponse sayHello(HelloRequest request);
}




