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
 * 解析<reference></reference>标签
 */
public class ReferenceBeanDefinitionParse implements BeanDefinitionParser {

    /**
     * <reference />标签用一个类封装
     */
    private Class<?> beanClass;

    public ReferenceBeanDefinitionParse(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    interface ReferenceConfig {

        public static String INTERFACE = "interface";

        public static String INTERF = "interf";

        public static String ID = "id";

        public static String PROTOCOL = "protocol";

        public static String LOADBALANCE = "loadBalance";

    }


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        String id = element.getAttribute(ReferenceConfig.ID);
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("reference id 不能为空！");
        }

        String interf = element.getAttribute(ReferenceConfig.INTERFACE);
        if (StringUtils.isEmpty(interf)) {
            throw new RuntimeException("reference interface 不能为空！");
        }

        String protocol = element.getAttribute(ReferenceConfig.PROTOCOL);
        String loadBalance = element.getAttribute(ReferenceConfig.LOADBALANCE);

        //RootBeanDefinition类会把<registry />转化为一个类
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        //spring会把这个beanClass进行实例化
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue(ReferenceConfig.INTERF, interf);
        beanDefinition.getPropertyValues().addPropertyValue(ReferenceConfig.ID, id);
        beanDefinition.getPropertyValues().addPropertyValue(ReferenceConfig.PROTOCOL, protocol);
        beanDefinition.getPropertyValues().addPropertyValue(ReferenceConfig.LOADBALANCE, loadBalance);
        //把这个对象注册到springBean工厂
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        return beanDefinition;

    }
}
