package com.mydubbo.loadbalance;

public class NodeInfo {
    private String host;

    private String port;

    private String contextpath;

    private String ref;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        this.contextpath = contextpath;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", contextpath='" + contextpath + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}
