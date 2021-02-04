package io.github.helloworlde.thrift;

import com.facebook.swift.service.ThriftEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
public class CustomThriftEventHandler extends ThriftEventHandler {

    @Override
    public void preRead(Object context, String methodName) throws TException {
        super.preRead(context, methodName);
        log.info("preRead, methodName: {}", methodName);
    }

    @Override
    public void postRead(Object context, String methodName, Object[] args) throws TException {
        super.postRead(context, methodName, args);
        log.info("postRead, methodName: {}", methodName);
    }

    @Override
    public void preWrite(Object context, String methodName, Object result) throws TException {
        super.preWrite(context, methodName, result);
        log.info("preWrite, methodName: {}", methodName);
    }

    @Override
    public void postWrite(Object context, String methodName, Object result) throws TException {
        super.postWrite(context, methodName, result);
        log.info("postWrite, methodName: {}", methodName);
    }

    @Override
    public void preWriteException(Object context, String methodName, Throwable t) throws TException {
        super.preWriteException(context, methodName, t);
        log.info("preWriteException, methodName: {}", methodName);
    }

    @Override
    public void postWriteException(Object context, String methodName, Throwable t) throws TException {
        super.postWriteException(context, methodName, t);
        log.info("postWriteException, methodName: {}", methodName);
    }
}
