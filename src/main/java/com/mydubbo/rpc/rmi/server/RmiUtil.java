package com.mydubbo.rpc.rmi.server;

import com.mydubbo.loadbalance.NodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiUtil {

    private static final Logger logger = LoggerFactory.getLogger(RmiUtil.class);
    /** 
     * @Description 启动rmi服务 服务绑定(在程序启动时就开始绑定)
     * @param @param host
     * @param @param port
     * @param @param id 参数 
     * @return void 返回类型  
     * @throws 
     */
    public void startRmiServer(String host, String port, String id) {
        try {
            RmiRemote rmiRemote = new RmiRemoteImp();
            LocateRegistry.createRegistry(Integer.valueOf(port));
            String rmiKey="rmi://" + host + ":" + port + "/" + id;
            Naming.bind(rmiKey, rmiRemote);
            logger.info("RMI服务器已经启动，信息{}",rmiKey);
        }
        catch (RemoteException e) {
            //远程访问异常
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            //地址有问题
            e.printStackTrace();
        }
        catch (AlreadyBoundException e) {
            //已经绑定过
            e.printStackTrace();
        }
    }


    /**
     * @Description RMI客戶端
     * @param @param host
     * @param @param port
     * @param @param id 参数
     * @return void 返回类型
     * @throws
     */
    public RmiRemote startRmiClient(String host, String port,String id) {
        try {
            String rmiKey="rmi://" + host + ":" + port + "/" + id;
            RmiRemote rmiRemote= (RmiRemote)Naming.lookup(rmiKey);
            return rmiRemote;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
