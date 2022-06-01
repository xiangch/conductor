package com.netflix.conductor.contribs.listener.amqp;

import com.netflix.conductor.core.listener.WorkflowStatusListener;
import com.netflix.conductor.core.listener.WorkflowStatusListenerStub;
import com.netflix.conductor.model.WorkflowModel;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 *  amqp
 * @author zengxc
 */
public class AmqpWorkflowStatusListener implements WorkflowStatusListener {


    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowStatusListenerStub.class);

    private final AmqpConnection amqpConnection;

    private final AmqpWorkflowStatusListenerProperties properties;

    private final String MESSAGE="{\"workflowId\":\"%s\",\"status\":\"%s\"}";

    public AmqpWorkflowStatusListener(AmqpConnection amqpConnection, AmqpWorkflowStatusListenerProperties properties) {
        this.amqpConnection = amqpConnection;
        this.properties = properties;
    }

    @Override
    public void onWorkflowCompleted(WorkflowModel workflow) {
        publishMessage(String.format(MESSAGE, workflow.getWorkflowId(),workflow.getStatus().toString()));
        LOGGER.debug("Workflow {} is completed", workflow.getWorkflowId());
    }

    @Override
    public void onWorkflowTerminated(WorkflowModel workflow) {
        publishMessage(String.format(MESSAGE, workflow.getWorkflowId(),workflow.getStatus().toString()));
        LOGGER.debug("Workflow {} is terminated", workflow.getWorkflowId());
    }

    @Override
    public void onWorkflowFinalized(WorkflowModel workflow) {
        LOGGER.debug("Workflow {} is finalized", workflow.getWorkflowId());
    }

    private void publishMessage(String message) {
        Channel channel = null;
        try {
            channel = amqpConnection.getOrCreateChannel();
            channel.basicPublish(properties.getExchange(), StringUtils.EMPTY, buildBasicProperties(), message.getBytes(properties.getContentEncoding()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                amqpConnection.returnChannel(channel);
            }
        }
    }

    private AMQP.BasicProperties buildBasicProperties() {
        return new AMQP.BasicProperties.Builder()
                .messageId(UUID.randomUUID().toString())
                .correlationId(UUID.randomUUID().toString())
                .contentType(properties.getContentType())
                .contentEncoding(properties.getContentEncoding())
                .deliveryMode(properties.getDeliveryMode())
                .build();
    }
}
