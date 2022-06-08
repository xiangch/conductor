/*
 * Copyright 2022 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.contribs.listener.amqp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.conductor.core.listener.WorkflowStatusListener;

import com.rabbitmq.client.*;

/**
 * @author zengxc
 */
@Configuration
@EnableConfigurationProperties(AmqpWorkflowStatusListenerProperties.class)
@ConditionalOnProperty(name = "conductor.workflow-status-listener.type", havingValue = "amqp")
public class AmqpWorkflowStatusListenerConfiguration {

    @Bean
    public WorkflowStatusListener getWorkflowStatusListener(
            AmqpWorkflowStatusListenerProperties properties) {
        AmqpConnection amqpConnection =
                new AmqpConnection(
                        buildConnectionFactory(properties), buildAddressesFromHosts(properties));
        return new AmqpWorkflowStatusListener(amqpConnection, properties);
    }

    private Address[] buildAddressesFromHosts(AmqpWorkflowStatusListenerProperties properties) {
        // Read hosts from config
        final String hosts = properties.getHosts();
        if (StringUtils.isEmpty(hosts)) {
            throw new IllegalArgumentException("Hosts are undefined");
        }
        return Address.parseAddresses(hosts);
    }

    private ConnectionFactory buildConnectionFactory(
            AmqpWorkflowStatusListenerProperties properties) {
        final ConnectionFactory factory = new ConnectionFactory();
        // Get rabbitmq username from config
        final String username = properties.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("Username is null or empty");
        } else {
            factory.setUsername(username);
        }
        // Get rabbitmq password from config
        final String password = properties.getPassword();
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password is null or empty");
        } else {
            factory.setPassword(password);
        }
        // Get vHost from config
        final String virtualHost = properties.getVirtualHost();
        ;
        if (StringUtils.isEmpty(virtualHost)) {
            throw new IllegalArgumentException("Virtual host is null or empty");
        } else {
            factory.setVirtualHost(virtualHost);
        }
        // Get server port from config
        final int port = properties.getPort();
        if (port <= 0) {
            throw new IllegalArgumentException("Port must be greater than 0");
        } else {
            factory.setPort(port);
        }
        final boolean useNio = properties.isUseNio();
        if (useNio) {
            factory.useNio();
        }
        factory.setConnectionTimeout(properties.getConnectionTimeoutInMilliSecs());
        factory.setRequestedHeartbeat(properties.getRequestHeartbeatTimeoutInSecs());
        factory.setNetworkRecoveryInterval(properties.getNetworkRecoveryIntervalInMilliSecs());
        factory.setHandshakeTimeout(properties.getHandshakeTimeoutInMilliSecs());
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        factory.setRequestedChannelMax(properties.getMaxChannelCount());
        return factory;
    }
}
