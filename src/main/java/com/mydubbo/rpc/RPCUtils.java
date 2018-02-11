package com.mydubbo.rpc;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangsong on 2018/2/11.
 */
public class RPCUtils {

    /**
     * 获取调用的方法
     *
     * @param bean
     * @param methodName
     * @param methodParamTypes
     * @return
     */
    public Method getMethod(Object bean, String methodName, JSONArray methodParamTypes) {

        //计算方法参数个数
        int paramTypeLength = 0;
        if (methodParamTypes != null && methodParamTypes.size() > 0) {
            paramTypeLength = methodParamTypes.size();
        }

        //得到对象的所有方法
        Method[] methods = bean.getClass().getMethods();

        //把方法名称一样的方法筛选出来
        List<Method> retMethod = new ArrayList<Method>();
        for (Method method : methods) {
            //把名字和methodName入参相同的方法加入到list中来
            if (methodName.trim().equals(method.getName())) {
                retMethod.add(method);
            }
        }
        methods = null;

        //如果大小是1就说明相同的方法只有一个
        if (retMethod.size() == 1) {
            return retMethod.get(0);
        }

        //判断参数个数是否一致
        boolean isSameSize = false;
        //判断参数类型是不是一样
        boolean isSameType = false;
        for (Method method : retMethod) {
            Class<?>[] types = method.getParameterTypes();
            //判断参数个数是否一致
            if (types.length == paramTypeLength) {
                isSameSize = true;
            }

            if (!isSameSize) {
                continue;
            }

            if (types.length == 0 && paramTypeLength == 0) {
                isSameType = true;
            } else {
                //判断参数类型是不是一样
                for (int i = 0; i < types.length; i++) {
                    if (types[i].toString().contains(methodParamTypes.getString(i))) {
                        isSameType = true;
                    } else {
                        isSameType = false;
                    }
                    if (!isSameType) {
                        break;
                    }
                }
            }
            //判断参数类型是不是一样
            if (isSameType) {
                return method;
            }
        }
        return null;
    }
}
