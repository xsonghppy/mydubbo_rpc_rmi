package com.mydubbo.rpc.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by xiangsong on 2018/2/11.
 */
public interface RmiRemote extends Remote {
    public String invoke(String param) throws RemoteException;
}
