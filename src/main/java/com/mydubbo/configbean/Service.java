package com.mydubbo.configbean;

import com.mydubbo.registry.util.RegistryUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by xiangsong on 2018/2/3.
 */
public class Service extends BaseConfigBean implements InitializingBean, ApplicationContextAware{

    private String interf;
    private String ref;
    private String protocol;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public static ApplicationContext getApplication() throws BeansException {
       return applicationContext;
    }

    /**
     * 在这个类初始化后执行的方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        RegistryUtil.addServiceToRegistryCenter(this,applicationContext);
    }

    public String getInterf() {
        return interf;
    }

    public void setInterf(String interf) {
        this.interf = interf;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "Service{" +
                "interf='" + interf + '\'' +
                ", ref='" + ref + '\'' +
                ", protocol='" + protocol + '\'' +
                '}';
    }


}
