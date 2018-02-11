package com.mydubbo.http.sender.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

/**
 */

public final class HttpsRequest implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(HttpsRequest.class);

    static class HttpsRequestConfig {
        public static final int DEFAULT_SOCKET_TIMETOUT = 3 * 1000;
        public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 2 * 1000;
        public static final int DEFAULT_CONNECT_TIME_OUT = 3 * 1000;
        public static final int DEFAULT_IDLE_CONNECTION_TIMEOUT = 10 * 60;
        public static final int DEFAULT_IDLE_CONNECTION_CHECK_TIME = 5;
        public static final int DEFAULT_CONNECTION_MAX_TOTAL = 500;
        public static final int DEFAULT_CONNECTION_MAX_PER_ROUTE = 280;
    }


    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;
    private PoolingHttpClientConnectionManager connectionManager;
    private IdleConnectionMonitorThread connectionMonitorThread;

    /**
     * How long to wait for the server to respond to various calls before
     * throwing a timeout exception 数据传输时间,默认是3秒,单位毫秒.
     */
    private int socketTimeoutInMillis = HttpsRequestConfig.DEFAULT_SOCKET_TIMETOUT;
    /**
     * How long to wait when trying to checkout a connection from the connection
     * pool before throwing an exception (the connection pool won't return
     * immediately if, for example, all the connections are checked out).
     * 获取http链接请求超时时间，默认2秒，单位毫秒.
     */
    private int connectionRequestTimeoutInMillis = HttpsRequestConfig.DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    /**
     * How long to wait for a connection to be established with the remote
     * server before throwing a timeout exception. 创建http链接超时时间,默认是3秒，单位毫秒.
     */
    private int connectTimeoutInMillis = HttpsRequestConfig.DEFAULT_CONNECT_TIME_OUT;

    /**
     * 空闲多久的链接自动关闭，默认是10分钟，单位秒
     */
    private int idleConnectionTimeoutInSecond = HttpsRequestConfig.DEFAULT_IDLE_CONNECTION_TIMEOUT;

    /**
     * 空闲连接监测周期,默认是5秒，单位秒
     */
    private int idleConnectionCheckedInSecond = HttpsRequestConfig.DEFAULT_IDLE_CONNECTION_CHECK_TIME;

    /**
     * The maximum number of connections allowed across all routes.
     */
    private int connectionMaxTotal = HttpsRequestConfig.DEFAULT_CONNECTION_MAX_TOTAL;
    /**
     * The maximum number of connections allowed for a route that has not been
     * specified otherwise by a call to setMaxPerRoute. Use setMaxPerRoute when
     * you know the route ahead of time and setDefaultMaxPerRoute when you do
     * not.
     */
    private int connectionMaxPerRoute = HttpsRequestConfig.DEFAULT_CONNECTION_MAX_PER_ROUTE;


    // 证书
    private boolean isCertificate = false;

    private String certLocalPath;

    private String certPassword;

    private String keyStoreName;

    private String[] certProcotocols;


    /**
     * 完全信任的HTTPS
     */
    public HttpsRequest() {
    }

    /**
     * 证书级的HTTPS
     *
     * @param certLocalPath
     * @param certPassword
     * @param certProcotocols
     */
    public HttpsRequest(String certLocalPath, String certPassword, String keyStoreName, String[] certProcotocols) {
        if (StringUtils.isBlank(certLocalPath)) {
            throw new IllegalArgumentException("证书路径参数不能为空!");
        }
        if (StringUtils.isBlank(certPassword)) {
            throw new IllegalArgumentException("证书密码参数不能为空!");
        }
        if (StringUtils.isBlank(keyStoreName)) {
            throw new IllegalArgumentException("证书加密算法名称参数不能为空!");
        }
        this.certLocalPath = certLocalPath;
        this.certPassword = certPassword;
        this.keyStoreName = keyStoreName;
        this.certProcotocols = certProcotocols;
        this.isCertificate = true;
    }

    public String sendJsonPost(String url, Object params, Charset charset) {
        Validate.notBlank(url, "请求地址URL参数不能为空!");
        Validate.notNull(params, "需转为json串的参数params对象不能为空!");
        Validate.notNull(charset, "请求编码对象charset参数不能为空!");
        String result = null;
        CloseableHttpResponse httpResponse = null;
        String postJsonStr = "";
        try {
            logger.debug("当前请求的URL：{}", url);

            if (params instanceof CharSequence) {
                postJsonStr = params.toString();
            } else {
                postJsonStr = JSONObject.toJSONString(params);
            }

            logger.debug("当前提交的JOSN串:{}", postJsonStr);

            HttpPost httpPost = this.createHttpPostForJson(url, postJsonStr, charset);
            long current = System.currentTimeMillis();

            HttpContext context = HttpClientContext.create();
            httpResponse = createHttpClient().execute(httpPost, context);
            HttpEntity responseEntity = httpResponse.getEntity();
            result = EntityUtils.toString(responseEntity, charset);

            logger.info("当前请求{}的速度:{} ms.", url, System.currentTimeMillis() - current);

            logger.debug("返回的JSON串:{}", result);
        } catch (Throwable e) {
            String errMsg = String.format("对%s发起请求失败\n!其中%s编码的json内容：%s", url, charset.name(), postJsonStr);
            logger.error(errMsg, e);
            throw new HttpsRequestException(errMsg, e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public String sendJsonGet(String url, Map<String, Object> param, Charset charset) {
        Validate.notBlank(url, "请求地址URL参数不能为空!");
        Validate.notNull(charset, "请求编码对象charset参数不能为空!");
        String result = null;
        CloseableHttpResponse httpResponse = null;
        try {
            if (param != null) {
                StringBuffer sb = new StringBuffer("?");
                for (Entry<String, Object> enty : param.entrySet()) {
                    sb.append(enty.getKey()).append("=").append(enty.getValue().toString()).append("&");
                }
                url = url + sb.toString();
            }

            logger.debug("当前请求的URL：{}", url);

            HttpGet httpGet = this.createHttpGetForJson(url, charset);
            long current = System.currentTimeMillis();

            httpResponse = createHttpClient().execute(httpGet);
            HttpEntity responseEntity = httpResponse.getEntity();
            result = EntityUtils.toString(responseEntity, charset);

            logger.info("当前请求{}的速度:{} ms.", url, System.currentTimeMillis() - current);

            logger.debug("返回的JSON串:{}", result);
        } catch (Throwable e) {
            String errMsg = String.format("对%s发起请求失败\n!编码为:%s", url, charset.name());
            logger.error(errMsg, e);
            throw new HttpsRequestException(errMsg, e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    private HttpPost createHttpPostForJson(String url, String postJsonStr, Charset charset) {
        HttpEntity postEntity = new StringEntity(postJsonStr, ContentType.create("application/json", charset));
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=" + charset.name());
        httpPost.setEntity(postEntity);
        httpPost.setConfig(createRequestConfig());
        return httpPost;
    }

    private HttpGet createHttpGetForJson(String url, Charset charset) {
        HttpGet httpPost = new HttpGet(url);
        httpPost.setHeader("Content-Type", "application/json; charset=" + charset.name());
        httpPost.setConfig(createRequestConfig());
        return httpPost;
    }

    private synchronized RequestConfig createRequestConfig() {
        if (requestConfig != null) {
            return requestConfig;
        }
        if (requestConfig == null) {
            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeoutInMillis)
                    .setConnectTimeout(connectTimeoutInMillis).setSocketTimeout(socketTimeoutInMillis).build();
        }
        return requestConfig;
    }

    private synchronized CloseableHttpClient createHttpClient() {
        if (httpClient != null) {
            return httpClient;
        }
        if (httpClient == null) {
            LayeredConnectionSocketFactory sslSocketFactory = isCertificate
                    ? this.createCertificateSSLSocketConnectionFactory()
                    : this.createFullTrustSSLConnectionSocketFactory();
            httpClient = HttpClients.custom().setConnectionManager(this.createConnectionManager(sslSocketFactory))
                    .setSSLSocketFactory(sslSocketFactory).build();
        }
        return httpClient;
    }

    private LayeredConnectionSocketFactory createFullTrustSSLConnectionSocketFactory() {
        LayeredConnectionSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (Throwable e) {
            throw new HttpsRequestException("初始化全信任SSLSocketFactory失败!", e);
        }
        return sslSocketFactory;
    }

    private LayeredConnectionSocketFactory createCertificateSSLSocketConnectionFactory() {
        LayeredConnectionSocketFactory sslSocketFactory = null;
        FileInputStream is = null;
        StringBuilder certificateInfo = new StringBuilder();
        certificateInfo.append("路径:").append(certLocalPath).append("|").append("密码:").append(certPassword).append("|")
                .append("加密算法:").append(keyStoreName).append("|").append("协议:")
                .append(ArrayUtils.toString(certProcotocols));
        try {
            logger.info("加载SSL证书,其信息-{}.", certificateInfo.toString());
            KeyStore keyStore = KeyStore.getInstance(keyStoreName);
            is = new FileInputStream(new File(certLocalPath));
            keyStore.load(is, certPassword.toCharArray());
            SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, certPassword.toCharArray()).build();
            sslSocketFactory = new SSLConnectionSocketFactory(sslContext, certProcotocols, null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        } catch (Throwable e) {
            throw new HttpsRequestException("初始化证书,SSLSocketFactory失败!信息:" + certificateInfo.toString(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return sslSocketFactory;
    }

    private PoolingHttpClientConnectionManager createConnectionManager(
            LayeredConnectionSocketFactory sslSocketFactory) {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https",
                        sslSocketFactory != null ? sslSocketFactory : SSLConnectionSocketFactory.getSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(connectionMaxTotal);
        connectionManager.setDefaultMaxPerRoute(connectionMaxPerRoute);

        startConnectionMonitorThread();

        return connectionManager;
    }

    private void startConnectionMonitorThread() {
        connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager, idleConnectionTimeoutInSecond,
                idleConnectionCheckedInSecond);
        connectionMonitorThread.setDaemon(true);
        connectionMonitorThread.start();
    }

    @Override
    public void close() {
        if (connectionMonitorThread != null) {
            connectionMonitorThread.shutdown();
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
            }
        }
    }
}
