package com.mydubbo.proxy;

import com.mydubbo.configbean.Reference;

import java.lang.reflect.Method;

/**
 * Created by xiangsong on 2018/2/4.
 * <p>
 * 调用方法的参数封装类
 */
public class Invocation {

    private Method method;

    private Object[] args;

    private Reference reference;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }
}
