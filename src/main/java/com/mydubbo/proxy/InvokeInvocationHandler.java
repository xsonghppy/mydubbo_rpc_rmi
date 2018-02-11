package com.mydubbo.proxy;

import com.mydubbo.configbean.Reference;
import com.mydubbo.rpc.Invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by xiangsong on 2018/2/4.
 */
public class InvokeInvocationHandler implements InvocationHandler {

    private Invoke invoke;
    private Reference reference;

    public InvokeInvocationHandler(Reference reference, Invoke invoke) {
        this.reference = reference;
        this.invoke = invoke;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation();
        invocation.setMethod(method);
        invocation.setArgs(args);
        invocation.setReference(reference);
        return  invoke.invoke(invocation);
    }
}
