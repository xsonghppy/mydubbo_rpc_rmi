package com.mydubbo.registry.util;

import com.mydubbo.configbean.Reference;
import com.mydubbo.configbean.Registry;
import com.mydubbo.configbean.Service;
import com.mydubbo.registry.impl.redis.RedisRegistryServiceImpl;
import com.mydubbo.registry.service.RegistryService;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangsong on 2018/2/4.
 */
public class RegistryUtil {

    interface RegisTryConfig {
        public static String REDIS = "redis";
        public static String ZOOKEEPER = "zookeeper";
    }

    private static Map<String, RegistryService> registryServiceMap = new HashMap<>();

    static {
        registryServiceMap.put(RegisTryConfig.REDIS, new RedisRegistryServiceImpl());
        registryServiceMap.put(RegisTryConfig.ZOOKEEPER, null);
    }


    /**
     * 添加服务到注册中心
     */
    public static void addServiceToRegistryCenter(Service service, ApplicationContext applicationContext) throws Exception {
        RegistryService registryService = getRegistryService(applicationContext);
        registryService.addService(service, applicationContext);
    }

    /**
     * 查询服务
     */
    public static List<String> getServiceFromRegistryCenter(Reference reference, ApplicationContext applicationContext) {
        RegistryService registryService = getRegistryService(applicationContext);
        return registryService.getSevice(reference, applicationContext);
    }

    private static RegistryService getRegistryService(ApplicationContext applicationContext) {
        //获取中心服务件信息
        Registry registry = applicationContext.getBean(Registry.class);
        if(registry==null){
            throw new RuntimeException("<<注册中心>> 没有配置注册中心服务件。");
        }

        //注册中心服务件类型 redis,zookeeper
        String protocol = registry.getProtocol();
        if (StringUtils.isEmpty(protocol)) {
            throw new RuntimeException("<<注册中心>> 注册中心服务件protocol没有配置值，请填写对应的值");
        }
        protocol = protocol.trim();
        RegistryService registryService = getRegistryService(protocol);
        if (registryService == null) {
            throw new RuntimeException("<<注册中心>> 没有找到对应的注册中心服务件,信息：" + registry);
        }
        return registryService;
    }


    private static RegistryService getRegistryService(String protocol) {
        return registryServiceMap.get(protocol.toLowerCase());
    }

}
