package com.mydubbo.http.sender.httpclient;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 连接池监测线程
 * 用于回收空闲、失效链接
 *
 */
public class IdleConnectionMonitorThread extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);
	private static final String CONNECTION_STATUS_LOGGER = IdleConnectionMonitorThread.class.getName()+".connectionStatus";
	private static final Logger connectionStatusLogger = LoggerFactory.getLogger(CONNECTION_STATUS_LOGGER);
	
	private final PoolingHttpClientConnectionManager connManager ;
	private final int idleConnectionTimeoutInSecond;
	private final int idleConnectionCheckTimeInSecond;
	private volatile boolean shutdown = false;
	
	public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connManager,int idleConnectionTimeoutInSecond, int idleConnectionCheckTimeInSecond){
		this.connManager = connManager;
		this.idleConnectionTimeoutInSecond = idleConnectionTimeoutInSecond;
		this.idleConnectionCheckTimeInSecond = idleConnectionCheckTimeInSecond;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(!shutdown){
				synchronized(this){
					logger.debug("开始检查空闲或已关闭连接.");
					
					wait(idleConnectionCheckTimeInSecond*1000);
					
					connManager.closeExpiredConnections();
					connManager.closeIdleConnections(idleConnectionTimeoutInSecond, TimeUnit.SECONDS);
					
					connectionStatusLogger.info("当前连接池状态:{}",connManager.getTotalStats().toString());					
					
					logger.debug("结束检查空闲或已关闭连接.");
				}
			}
		}catch(InterruptedException e){
			//logger.error(msg);
		}
		logger.debug("检查连接线程已经关闭。");
	}
	
	
	public void shutdown(){
		logger.debug("开始执行关闭检查连接线程..");
		shutdown = true;
		synchronized(this){
			notifyAll();
		}
	}
	

}
