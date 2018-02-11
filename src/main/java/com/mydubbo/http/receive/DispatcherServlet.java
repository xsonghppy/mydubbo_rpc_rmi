package com.mydubbo.http.receive;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mydubbo.configbean.Service;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 这个是soa框架中给生产者接收请求用的servlet，这个必须是采用http协议才能掉得到
 */

public class DispatcherServlet extends HttpServlet {


    private static final long serialVersionUID = 2368065256546765L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //获取传递的值
        JSONObject requestparam = this.httpProcess(req);
        if (requestparam == null) {
            String msg = "no param,please check API...";
            this.setReturnValues(resp, msg);
            return;
        }

        //要从远程的生产者的spring容器中拿到对应的serviceid实例
        String serviceId = requestparam.getString("serviceId");
        ApplicationContext application = Service.getApplication();
        //服务层的实例
        Object serviceBean = application.getBean(serviceId);
        if (serviceBean == null) {
            String msg = "NoSuchInterface instance,please check Interface...";
            this.setReturnValues(resp, msg);
            return;
        }

        //方法名称
        String methodName = requestparam.getString("methodName");
        //方法参数类型串
        JSONArray methodParamTypes = requestparam.getJSONArray("methodParamType");

        //方法的获取，要考虑方法的重载
        Method method = this.getMethod(serviceBean, methodName, methodParamTypes);
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
            this.setReturnValues(resp, msg);
        } else {
            String msg = "NoSuchMethod,please check API...";
            this.setReturnValues(resp, msg);
        }
    }

    /**
     * 输出信息
     *
     * @param resp
     * @param msg
     * @throws IOException
     */
    private void setReturnValues(HttpServletResponse resp, String msg) throws IOException {
        resp.setHeader("Content-Type", "application/json; charset=utf-8");
        PrintWriter pw = resp.getWriter();
        pw.write(msg);
        pw.flush();
        pw.close();
    }


    /**
     * 获取调用的方法
     *
     * @param bean
     * @param methodName
     * @param methodParamTypes
     * @return
     */
    private Method getMethod(Object bean, String methodName, JSONArray methodParamTypes) {

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

    /**
     * 获取传递的值
     *
     * @param req
     * @return
     * @throws IOException
     */
    private JSONObject httpProcess(HttpServletRequest req) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStream is = req.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String s = "";
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        is.close();
        br.close();
        if (sb.toString().length() <= 0) {
            return null;
        } else {
            return JSONObject.parseObject(sb.toString());
        }
    }

}
