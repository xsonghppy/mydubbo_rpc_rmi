package com.mydubbo.rpc.rmi.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mydubbo.configbean.Service;
import com.mydubbo.rpc.RPCUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by xiangsong on 2018/2/11.
 * <p>
 * rmi的实现类，负责rmi的调用 ,这个是生产者端使用的类(服务调用的类)
 */
public class RmiRemoteImp extends UnicastRemoteObject implements RmiRemote {

    protected RmiRemoteImp() throws RemoteException {
        super();
    }

    private static final long serialVersionUID = 890890693549L;

    private RPCUtils rpcUtils = new RPCUtils();

    @Override
    public String invoke(String param) throws RemoteException {
        if (StringUtils.isBlank(param)) {
            String msg = "no param,please check API...";
            return msg;
        }

        //获取传递的值
        JSONObject requestparam = JSONObject.parseObject(param);
        if (requestparam == null) {
            String msg = "no param,please check API...";
            return msg;
        }

        //要从远程的生产者的spring容器中拿到对应的serviceid实例
        String serviceId = requestparam.getString("serviceId");
        ApplicationContext application = Service.getApplication();
        //服务层的实例
        Object serviceBean = application.getBean(serviceId);
        if (serviceBean == null) {
            String msg = "NoSuchInterface instance,please check Interface...";
            return msg;
        }

        //方法名称
        String methodName = requestparam.getString("methodName");
        //方法参数类型串
        JSONArray methodParamTypes = requestparam.getJSONArray("methodParamType");

        //方法的获取，要考虑方法的重载
        Method method = this.rpcUtils.getMethod(serviceBean, methodName, methodParamTypes);
        if (method != null) {
            //传递参数值
            JSONArray paramValues = requestparam.getJSONArray("paramValues");
            //调用方法传递的值
            Object[] paramObject = null;
            if (paramValues != null && paramValues.size() > 0) {
                paramObject = new Object[paramValues.size()];
                int i = 0;
                for (Object o : paramValues) {
                    paramObject[i++] = o;
                }
            }
            Object result = null;
            try {
                result = method.invoke(serviceBean, paramObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            String msg = JSONObject.toJSONString(result);
            return msg;
        } else {
            String msg = "NoSuchMethod,please check API...";
            return msg;
        }
    }


}
