package com.mydubbo.loadbalance;

/**
 * Created by xiangsong on 2018/2/5.
 */
public class LoadBalanceUtil {

    public static LoadBalance getSelect(String loadBalance) {
        if (loadBalance.equalsIgnoreCase("random")) {
            return new RandomLoadBalance();
        } else if (loadBalance.equalsIgnoreCase("round")) {
            return new RoundRobinLoadBalance();
        } else {
            return new RandomLoadBalance();
        }
    }
}
