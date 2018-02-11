package com.mydubbo.redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.mydubbo.configbean.Protocol;
import com.mydubbo.configbean.Service;

/**
 * Created by xiangsong on 2018/2/4.
 */
public class RedisTest {

    public static void main(String[] args) {
        String ip="192.168.0.107";
        int port =6379;
        //RedisPoolerUtil.initJdeisPool(ip,port,null);

        String key="areaService";
        Service service=new Service();
        service.setInterf("com.mydubbo.areaService");
        service.setProtocol("http");
        service.setRef(key);

        Service service1=new Service();
        service1.setInterf("com.mydubbo.areaService");
        service1.setProtocol("netty");
        service1.setRef(key);

        Protocol protocol=new Protocol();
        protocol.setName("http");
        protocol.setIp(ip);
        protocol.setPort("1126");

        Protocol protocol1=new Protocol();
        protocol1.setName("netty");
        protocol1.setIp(ip);
        protocol1.setPort("1127");

        String address=protocol.getIp()+":"+protocol.getPort();
        String address1=protocol1.getIp()+":"+protocol1.getPort();

        JSONObject jsonpObject=new JSONObject();
        jsonpObject.put("service",JSONObject.toJSONString(service));
        jsonpObject.put("protocol",JSONObject.toJSONString(protocol));

        JSONObject jsonpObject1=new JSONObject();
        jsonpObject1.put("service",JSONObject.toJSONString(service1));
        jsonpObject1.put("protocol",JSONObject.toJSONString(protocol));

        JSONArray jsonArr=new JSONArray();
        jsonArr.add(jsonpObject);
        jsonArr.add(jsonpObject1);



        JSONObject jsonp=new JSONObject();
        jsonp.put(address,jsonArr);
        jsonp.put(address1,jsonArr);

        JSONObject js=new JSONObject();
        js.put(key,jsonp);

        String jsStr=js.toJSONString();
        System.out.println(jsStr);

        JSONObject obj= (JSONObject) JSONObject.parse(jsStr);

        obj.keySet();
        System.out.println(obj.toJSONString());




    }
}
