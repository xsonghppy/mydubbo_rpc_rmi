package com.mydubbo.invoke.impl;

import com.alibaba.fastjson.JSONObject;
import com.mydubbo.configbean.Reference;
import com.mydubbo.invoke.Invoke;
import com.mydubbo.loadbalance.LoadBalance;
import com.mydubbo.loadbalance.LoadBalanceUtil;
import com.mydubbo.loadbalance.NodeInfo;
import com.mydubbo.proxy.Invocation;
import com.mydubbo.rpc.rmi.server.RmiRemote;
import com.mydubbo.rpc.rmi.server.RmiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by xiangsong on 2018/2/4.
 */
public class RMIInvoke implements Invoke {

    private static final Logger logger = LoggerFactory.getLogger(RMIInvoke.class);

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
        Method method = invocation.getMethod();
        Object param[] = invocation.getArgs();
        JSONObject params = new JSONObject();
        params.put("methodName", method.getName());
        params.put("methodParamType", method.getParameterTypes());
        params.put("paramValues", param);
        params.put("serviceId", nodeInfo.getRef());
        RmiUtil rmiUtil = new RmiUtil();
        String rmiKey = "mydubboRMI";
        String result = null;
        long starTime = System.currentTimeMillis();
        //获取RMI对象
        String ip = nodeInfo.getHost();
        String port = nodeInfo.getPort();
        String rmiUrl = "rmi://" + ip + ":" + port + "/" + rmiKey;
        logger.info("请求的RMI客户端信息:{},当前提交的JOSN串:{}", rmiUrl, params);
        RmiRemote rmiRemote = rmiUtil.startRmiClient(ip, port, rmiKey);
        if (rmiRemote != null) {
            result = rmiRemote.invoke(JSONObject.toJSONString(params));
        }
        logger.info("请求的RMI客户端信息:{},提交的JOSN串:{}, 返回的JOSN串：{},响应时间:{}ms", rmiUrl, params, result, System.currentTimeMillis() - starTime);
        return result;
    }
}
