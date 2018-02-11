package com.mydubbo.spring.parse;

import com.mydubbo.configbean.Protocol;
import com.mydubbo.configbean.Reference;
import com.mydubbo.configbean.Registry;
import com.mydubbo.configbean.Service;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Created by xiangsong on 2018/2/3.
 * <p>
 * spring解析配置文件的主类
 */
public class SOANamespaceHandler extends NamespaceHandlerSupport {

    public SOANamespaceHandler() {

    }

    @Override
    public void init() {

        //注册中心解析器
        this.registerBeanDefinitionParser("registry", new RegistryBeanDefinitionParse(Registry.class));
        //协议解析器
        this.registerBeanDefinitionParser("protocol", new ProtocolBeanDefinitionParse(Protocol.class));
        //生产者解析器
        this.registerBeanDefinitionParser("service",new ServiceBeanDefinitionParse(Service.class));
        //消费者解析器
        this.registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParse(Reference.class));

    }
}
