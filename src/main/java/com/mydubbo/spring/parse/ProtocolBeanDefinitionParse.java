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
 * 解析<protocol></protocol>标签
 */
public class ProtocolBeanDefinitionParse implements BeanDefinitionParser {

    /**
     * <protocol />标签用一个类封装
     */
    private Class<?> beanClass;

    public ProtocolBeanDefinitionParse(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    interface ProtocolConfig {

        public final static String NAME = "name";

        public final static String IP = "ip";

        public final static String PORT = "port";

        public final static String CONTEXTPATH = "contextpath";
    }


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String name = element.getAttribute(ProtocolConfig.NAME);
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("Protocol name 不能为空！");
        }

        String ip = element.getAttribute(ProtocolConfig.IP);
        if (StringUtils.isEmpty(ip)) {
            throw new RuntimeException("Protocol ip 不能为空！");
        }

        String portStr = element.getAttribute(ProtocolConfig.PORT);
        int port = 0;
        if (StringUtils.isEmpty(portStr)) {
            throw new RuntimeException("Protocol port 不能为空！");
        } else {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Protocol port 只能是数字！");
            }
        }

        String contextpath = element.getAttribute(ProtocolConfig.CONTEXTPATH);

        //RootBeanDefinition类会把<registry />转化为一个类
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        //spring会把这个beanClass进行实例化
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue(ProtocolConfig.NAME, name);
        beanDefinition.getPropertyValues().addPropertyValue(ProtocolConfig.IP, ip);
        beanDefinition.getPropertyValues().addPropertyValue(ProtocolConfig.PORT, port);
        beanDefinition.getPropertyValues().addPropertyValue(ProtocolConfig.CONTEXTPATH, contextpath);
        //把这个对象注册到springBean工厂
        parserContext.getRegistry().registerBeanDefinition("protocol" + ip + ":" + port, beanDefinition);
        return beanDefinition;

    }
}
