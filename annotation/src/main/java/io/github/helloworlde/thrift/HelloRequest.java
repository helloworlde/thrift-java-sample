package io.github.helloworlde.thrift;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.Data;

@Data
@ThriftStruct
public class HelloRequest {

    @ThriftConstructor
    public HelloRequest() {
    }

    @ThriftField(1)
    public String message;

}
