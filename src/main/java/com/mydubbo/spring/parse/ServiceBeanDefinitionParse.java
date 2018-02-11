package com.mydubbo.spring.parse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.UUID;

/**
 * Created by xiangsong on 2018/2/3.
 * <p>
 * 解析<service></service>标签
 */
public class ServiceBeanDefinitionParse implements BeanDefinitionParser {

    /**
     * <service />标签用一个类封装
     */
    private Class<?> beanClass;

    public ServiceBeanDefinitionParse(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    interface ServiceConfig {

        public static String INTERFACE = "interface";

        public static String INTERF = "interf";

        public static String REF = "ref";

        public static String PROTOCOL = "protocol";
    }


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String interf = element.getAttribute(ServiceConfig.INTERFACE);
        if (StringUtils.isEmpty(interf)) {
            throw new RuntimeException("service interface 不能为空！");
        }

        String ref = element.getAttribute(ServiceConfig.REF);
        if (StringUtils.isEmpty(ref)) {
            throw new RuntimeException("service ref 不能为空！");
        }

        String protocol = element.getAttribute(ServiceConfig.PROTOCOL);

        //RootBeanDefinition类会把<registry />转化为一个类
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        //spring会把这个beanClass进行实例化
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue(ServiceConfig.INTERF, interf);
        beanDefinition.getPropertyValues().addPropertyValue(ServiceConfig.REF, ref);
        beanDefinition.getPropertyValues().addPropertyValue(ServiceConfig.PROTOCOL, protocol);
        //把这个对象注册到springBean工厂
        parserContext.getRegistry().registerBeanDefinition(UUID.randomUUID().toString(), beanDefinition);
        return beanDefinition;

    }
}
