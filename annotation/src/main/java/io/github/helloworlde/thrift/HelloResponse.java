package io.github.helloworlde.thrift;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.Data;

import java.util.List;

@Data
@ThriftStruct
public class HelloResponse {

    @ThriftConstructor
    public HelloResponse() {
    }

    @ThriftField(1)
    public String message;

    @ThriftField(2)
    public List<String> details;
}
