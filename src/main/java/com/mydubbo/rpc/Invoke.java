package com.mydubbo.rpc;


import com.mydubbo.proxy.Invocation;

/**
 * Created by xiangsong on 2018/2/4.
 */
public interface Invoke {
    /**
     * 调用方法
     * invocation 调用的参数封装类
     *
     * @return
     * @throws Exception
     */
    Object invoke(Invocation invocation) throws Exception;
}
