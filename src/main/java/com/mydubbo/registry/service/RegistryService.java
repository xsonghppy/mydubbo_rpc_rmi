package com.mydubbo.registry.service;

import com.mydubbo.configbean.Reference;
import com.mydubbo.configbean.Service;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * Created by xiangsong on 2018/2/4.
 */
public interface RegistryService {

    /**
     * 注册服务
     */
    void addService(Service service, ApplicationContext applicationContext);

    /**
     * 发现服务
     */
    List<String> getSevice(Reference reference, ApplicationContext applicationContext);
}
