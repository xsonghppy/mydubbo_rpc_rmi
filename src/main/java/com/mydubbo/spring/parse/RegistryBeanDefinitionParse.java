package com.mydubbo.spring.parse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by xiangsong on 2018/2/3.
 * <p>
 * 解析<registry></registry>标签
 */
public class RegistryBeanDefinitionParse implements BeanDefinitionParser {

    /**
     * <registry />标签用一个类封装
     */
    private Class<?> beanClass;

    public RegistryBeanDefinitionParse(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    interface RegistryConfig {

        public static String PROTOCOL = "protocol";

        public static String IP = "ip";

        public static String PORT = "port";
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String protocol = element.getAttribute(RegistryConfig.PROTOCOL);
        if (StringUtils.isEmpty(protocol)) {
            throw new RuntimeException("registry protocol 不能为空！");
        }

        String ip = element.getAttribute(RegistryConfig.IP);
        if (StringUtils.isEmpty(ip)) {
            throw new RuntimeException("registry ip 不能为空！");
        }

        String portStr = element.getAttribute(RegistryConfig.PORT);
        int port = 0;
        if (StringUtils.isEmpty(portStr)) {
            throw new RuntimeException("registry port 不能为空！");
        } else {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("registry port 只能是数字！");
            }
        }

        //RootBeanDefinition类会把<registry />转化为一个类
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        //spring会把这个beanClass进行实例化
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue(RegistryConfig.PROTOCOL, protocol);
        beanDefinition.getPropertyValues().addPropertyValue(RegistryConfig.IP, ip);
        beanDefinition.getPropertyValues().addPropertyValue(RegistryConfig.PORT, port);
        //把这个对象注册到springBean工厂
        parserContext.getRegistry().registerBeanDefinition("registry" + ip + ":" + port, beanDefinition);
        return beanDefinition;

    }
}
