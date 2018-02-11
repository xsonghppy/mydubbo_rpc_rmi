package com.mydubbo.configbean;

import com.mydubbo.proxy.InvokeInvocationHandler;
import com.mydubbo.registry.util.RegistryUtil;
import com.mydubbo.rpc.Invoke;
import com.mydubbo.rpc.http.HttpInvoke;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangsong on 2018/2/3.
 * <p>
 * InitializingBean 类初始化完成如果调用：afterPropertiesSet（）方法
 * <p>
 * FactoryBean spring容器的bean工厂 applicationContext.getBean()
 * <p>
 * ApplicationContextAware 获取spring上下文
 */
public class Reference extends BaseConfigBean implements InitializingBean, FactoryBean, ApplicationContextAware {
    private String id;
    private String interf;

    private String protocol;
    private String loadBalance;
    private ApplicationContext applicationContext;
    private List<String> serviceList;
    /**
     * 实现调用的远程接口
     */
    private Invoke invoke;

    private static Map<String, Invoke> invokeMap = new HashMap<String, Invoke>();

    static {
        invokeMap.put("http", new HttpInvoke());
    }

    /**
     * 获取接口的代理对象
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getObject() throws Exception {
        if (!StringUtils.isEmpty(protocol)) {
            invoke = invokeMap.get(protocol);
        } else {
            Protocol pro = applicationContext.getBean(Protocol.class);
            if (pro != null) {
                invoke = invokeMap.get(pro.getName());
            } else {
                invoke = invokeMap.get("http");
            }
        }
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{Class.forName(interf)}, new InvokeInvocationHandler(this, invoke));
    }

    @Override
    public Class<?> getObjectType() {
        try {
            if (!StringUtils.isEmpty(interf)) {
                return Class.forName(interf);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 在这个类初始化后执行的方法
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        serviceList = RegistryUtil.getServiceFromRegistryCenter(this, applicationContext);
        System.out.println(serviceList);
    }

    public String getInterf() {
        return interf;
    }

    public void setInterf(String interf) {
        this.interf = interf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public List<String> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<String> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "interf='" + interf + '\'' +
                ", id='" + id + '\'' +
                ", protocol='" + protocol + '\'' +
                ", loadBalance='" + loadBalance + '\'' +
                '}';
    }


}
