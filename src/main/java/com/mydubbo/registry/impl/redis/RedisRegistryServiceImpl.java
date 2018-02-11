package com.mydubbo.registry.impl.redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.mydubbo.configbean.Protocol;
import com.mydubbo.configbean.Reference;
import com.mydubbo.configbean.Registry;
import com.mydubbo.configbean.Service;
import com.mydubbo.redis.RedisPoolerUtil;
import com.mydubbo.registry.service.RegistryService;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xiangsong on 2018/2/4.
 * <p>
 * redis服务注册类
 */
public class RedisRegistryServiceImpl implements RegistryService {

    /**
     * 注册服务
     */
    @Override
    public void addService(Service service, ApplicationContext applicationContext) {
        //服务协议和服务访问IP地址
        Protocol protocol = applicationContext.getBean(Protocol.class);
        JSONObject serviceJson = new JSONObject();
        serviceJson.put("service", JSONObject.toJSONString(service));
        serviceJson.put("protocol", JSONObject.toJSONString(protocol));
        this.lpush(service, protocol, serviceJson);
    }

    private void lpush(Service service, Protocol protocol, JSONObject serviceJson) {
        String address = protocol.getIp() + ":" + protocol.getPort();
        String key = service.getInterf();
        if (RedisPoolerUtil.exists(key)) {
            List<String> newRegistry = new ArrayList<String>();
            String newProtocol = service.getProtocol();
            //获取服务的所以注册列表
            List<String> registryInfo = RedisPoolerUtil.lrange(key);
            boolean isOldNode = false;
            for (String node : registryInfo) {
                JSONObject jo = JSONObject.parseObject(node);
                //判断某个服务接口在这台机在有没有注册过
                if (jo.containsKey(address)) {
                    isOldNode = true;
                    JSONArray serArr = (JSONArray) jo.get(address);
                    boolean flag = false;
                    for (int i = 0; i < serArr.size(); i++) {
                        JSONObject serJson = (JSONObject) serArr.get(i);
                        JSONObject serviceInterfaceJson = serJson.getJSONObject("service");
                        String oldProtocol = serviceInterfaceJson.getString("protocol");
                        if (newProtocol.equalsIgnoreCase(oldProtocol)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        serArr.add(serviceJson);
                        JSONObject ipportJson = new JSONObject();
                        ipportJson.put(address, serArr);
                        newRegistry.add(ipportJson.toJSONString());
                    } else {
                        newRegistry.add(node);
                    }
                } else {
                    newRegistry.add(node);
                }
            }
            if (isOldNode) {
                if (newRegistry.size() > 0) {
                    RedisPoolerUtil.del(key);
                    String[] newReStr = new String[newRegistry.size()];
                    for (int i = 0; i < newRegistry.size(); i++) {
                        newReStr[i] = newRegistry.get(i);
                    }
                    RedisPoolerUtil.lpush(key, newReStr);
                }
            } else {
                JSONArray jsonArr = new JSONArray();
                jsonArr.add(serviceJson);
                JSONObject jsonp = new JSONObject();
                jsonp.put(address, jsonArr);
                RedisPoolerUtil.lpush(key, jsonp.toJSONString());
            }
        } else {
            //所有的都是第一次啟動
            JSONArray jsonArr = new JSONArray();
            jsonArr.add(serviceJson);
            JSONObject jsonp = new JSONObject();
            jsonp.put(address, jsonArr);
            RedisPoolerUtil.lpush(key, jsonp.toJSONString());
        }
    }


    /**
     * 服务发现
     */
    @Override
    public List<String> getSevice(Reference reference, ApplicationContext applicationContext) {
        List<String> newRegistry = new ArrayList<String>();

        String key = reference.getInterf();
        String newProtocol = reference.getProtocol();

        if (StringUtils.isEmpty(newProtocol)){
            //服务协议和服务访问IP地址
            Protocol protocol = applicationContext.getBean(Protocol.class);
            if(protocol!=null){
                newProtocol=protocol.getName();
            }
        }

        if (RedisPoolerUtil.exists(key)) {
            //获取服务的所以注册列表
            List<String> registryInfo = RedisPoolerUtil.lrange(key);
            for (String node : registryInfo) {
                JSONObject jo = JSONObject.parseObject(node);
                Set<String> joKeySet=jo.keySet();
                String address=null;
                //循环==1
                for (String ipProt : joKeySet) {
                    address = ipProt;
                }

                JSONArray serArr = (JSONArray) jo.get(address);
                for (int i = 0; i < serArr.size(); i++) {
                    JSONObject serJson = (JSONObject) serArr.get(i);
                    JSONObject serviceInterfaceJson = serJson.getJSONObject("service");
                    String oldProtocol = serviceInterfaceJson.getString("protocol");
                    if (StringUtils.isEmpty(oldProtocol)) {
                        JSONObject protocolInterfaceJson = serJson.getJSONObject("protocol");
                        oldProtocol = protocolInterfaceJson.getString("name");
                    }
                    if (newProtocol.equalsIgnoreCase(oldProtocol)) {
                        newRegistry.add(serJson.toJSONString());
                        break;
                    }
                }
            }

        }
        return newRegistry;
    }
}
