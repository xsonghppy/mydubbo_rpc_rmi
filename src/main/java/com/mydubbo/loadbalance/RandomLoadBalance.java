package com.mydubbo.loadbalance;

import com.alibaba.fastjson.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * 随机的负载均衡算法
 */

public class RandomLoadBalance implements LoadBalance {

    public NodeInfo doSelect(List<String> registryInfo) {
        Random random = new Random();
        int index = random.nextInt(registryInfo.size());
        String registry = registryInfo.get(index);
        JSONObject registryJo = JSONObject.parseObject(registry);
        JSONObject protocol = registryJo.getJSONObject("protocol");
        JSONObject service = registryJo.getJSONObject("service");

        NodeInfo nodeinfo = new NodeInfo();
        nodeinfo.setHost(protocol.get("ip") != null ? protocol.getString("ip"): "");
        nodeinfo.setPort(protocol.get("port") != null ? protocol.getString("port") : "");
        nodeinfo.setContextpath(protocol.get("contextpath") != null ? protocol.getString("contextpath"): "");
        nodeinfo.setRef(service.get("ref") != null ? service.getString("ref"): "");
        return nodeinfo;
    }
}
