package com.netflix.conductor.contribs.listener.amqp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zengxc
 */
@ConfigurationProperties("conductor.workflow-status-listener.amqp")
public class AmqpWorkflowStatusListenerProperties {

    private String hosts = ConnectionFactory.DEFAULT_HOST;

    private String username = ConnectionFactory.DEFAULT_USER;

    private String password = ConnectionFactory.DEFAULT_PASS;

    private String virtualHost = ConnectionFactory.DEFAULT_VHOST;

    private String exchange = StringUtils.EMPTY;

    private int port = AMQP.PROTOCOL.PORT;

    private String contentType = "application/json";

    private String contentEncoding = "UTF-8";

    private int deliveryMode = 2;
    private int connectionTimeoutInMilliSecs = 180000;
    private int networkRecoveryIntervalInMilliSecs = 5000;
    private int requestHeartbeatTimeoutInSecs = 30;
    private int handshakeTimeoutInMilliSecs = 180000;
    private int maxChannelCount = 5000;

    private boolean useNio = false;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public boolean isUseNio() {
        return useNio;
    }

    public void setUseNio(boolean useNio) {
        this.useNio = useNio;
    }

    public int getConnectionTimeoutInMilliSecs() {
        return connectionTimeoutInMilliSecs;
    }

    public void setConnectionTimeoutInMilliSecs(int connectionTimeoutInMilliSecs) {
        this.connectionTimeoutInMilliSecs = connectionTimeoutInMilliSecs;
    }

    public int getHandshakeTimeoutInMilliSecs() {
        return handshakeTimeoutInMilliSecs;
    }

    public void setHandshakeTimeoutInMilliSecs(int handshakeTimeoutInMilliSecs) {
        this.handshakeTimeoutInMilliSecs = handshakeTimeoutInMilliSecs;
    }

    public int getMaxChannelCount() {
        return maxChannelCount;
    }

    public void setMaxChannelCount(int maxChannelCount) {
        this.maxChannelCount = maxChannelCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRequestHeartbeatTimeoutInSecs() {
        return requestHeartbeatTimeoutInSecs;
    }

    public void setRequestHeartbeatTimeoutInSecs(int requestHeartbeatTimeoutInSecs) {
        this.requestHeartbeatTimeoutInSecs = requestHeartbeatTimeoutInSecs;
    }

    public int getNetworkRecoveryIntervalInMilliSecs() {
        return networkRecoveryIntervalInMilliSecs;
    }

    public void setNetworkRecoveryIntervalInMilliSecs(int networkRecoveryIntervalInMilliSecs) {
        this.networkRecoveryIntervalInMilliSecs = networkRecoveryIntervalInMilliSecs;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
