package com.mydubbo.rpc.http;

import com.alibaba.fastjson.JSONObject;
import com.mydubbo.configbean.Reference;
import com.mydubbo.http.sender.httpclient.HttpsRequest;
import com.mydubbo.loadbalance.LoadBalance;
import com.mydubbo.loadbalance.LoadBalanceUtil;
import com.mydubbo.loadbalance.NodeInfo;
import com.mydubbo.proxy.Invocation;
import com.mydubbo.rpc.Invoke;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * Created by xiangsong on 2018/2/4.
 */
public class HttpInvoke implements Invoke {


    private HttpsRequest httpsRequest=new HttpsRequest();

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Reference reference = invocation.getReference();
        String loadbalanceType = reference.getLoadBalance();
        LoadBalance loadBalance = LoadBalanceUtil.getSelect(loadbalanceType);
        NodeInfo nodeInfo = loadBalance.doSelect(reference.getServiceList());

        //我们调用远程的生产者是传输的json字符串
        //根据serviceid去对端生产者的spring容器中获取serviceid对应的实例
        //根据methodName和methodType获取实例的method对象
        //然后反射调用method方法
        Method method=invocation.getMethod();
        Object param[]=invocation.getArgs();
        JSONObject params=new JSONObject();
        params.put("methodName",method.getName());
        params.put("methodParamType",method.getParameterTypes());
        params.put("paramValues",param);
        params.put("serviceId",nodeInfo.getRef());
        System.out.println(nodeInfo+"--------------"+method+"========"+param);
        String url="http://"+nodeInfo.getHost()+":"+nodeInfo.getPort()+"/"+nodeInfo.getContextpath();
        String result= httpsRequest.sendJsonPost(url,params, Charset.forName("utf-8"));
        return result;
    }
}
