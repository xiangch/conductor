package com.netflix.conductor.contribs.listener.amqp;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author zengxc
 */
public class AmqpConnection {

    private static final Set<Channel> availableChannelPool =
            new HashSet<>();
    private static final String CLIENT_PROVIDED_NAME = "WorkflowStatusListener";

    private static Logger LOGGER = LoggerFactory.getLogger(AmqpConnection.class);

    private ConnectionFactory factory = null;
    private Address[] addresses = null;

    private volatile Connection publisherConnection = null;

    private AmqpConnection() {
    }

    public AmqpConnection(final ConnectionFactory factory, final Address[] address) {
        this.factory = factory;
        this.addresses = address;
    }

    public Channel getOrCreateChannel() throws Exception {
        synchronized (this) {
            if (publisherConnection == null || !publisherConnection.isOpen()) {
                publisherConnection = createConnection();
            }
        }
        return borrowChannel(publisherConnection);
    }

    private Channel getOrCreateChannel(Connection rmqConnection) {
        // Channel creation is required
        Channel locChn = null;

            try {
                LOGGER.debug("Creating a channel");
                locChn = rmqConnection.createChannel();
                if (locChn == null || !locChn.isOpen()) {
                    throw new RuntimeException("Fail to open a channel");
                }
                locChn.addShutdownListener(
                        cause -> {
                            LOGGER.error(
                                    " Channel has been shutdown: {}",
                                    cause.getMessage(),
                                    cause);
                        });
                return locChn;
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Cannot open a"
                                + " channel on "
                                + Arrays.stream(addresses)
                                .map(address -> address.toString())
                                .collect(Collectors.joining(",")),
                        e);
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Cannot open a"
                                + " channel on "
                                + Arrays.stream(addresses)
                                .map(address -> address.toString())
                                .collect(Collectors.joining(",")),
                        e);
            }
    }

    /**
     * borrowChannel -> returnChannel
     * @param rmqConnection
     * @return
     * @throws Exception
     */
    private synchronized Channel borrowChannel(Connection rmqConnection) throws Exception {
        if (availableChannelPool.isEmpty()) {
            Channel channel = getOrCreateChannel(rmqConnection);
            LOGGER.info("Channels are not available in the pool. Created a channel");
            return channel;
        }
        Iterator<Channel> itr = availableChannelPool.iterator();
        while (itr.hasNext()) {
            Channel channel = itr.next();
            if (channel != null && channel.isOpen()) {
                itr.remove();
                LOGGER.info("Borrowed the channel object from the channel pool");
                return channel;
            } else {
                itr.remove();
            }
        }
        Channel channel = getOrCreateChannel(rmqConnection);
        LOGGER.info("No proper channels available in the pool. Created a channel");
        return channel;
    }

    private Connection createConnection() {
        try {
            Connection connection =
                    factory.newConnection(
                            addresses, System.getenv("HOSTNAME") + "-" + CLIENT_PROVIDED_NAME);
            if (connection == null || !connection.isOpen()) {
                throw new RuntimeException("Failed to open connection");
            }
            connection.addShutdownListener(
                    cause -> LOGGER.error(
                            "Received a shutdown exception for the connection {}. reason {} cause{}",
                            connection.getClientProvidedName(),
                            cause.getMessage(),
                            cause));
            connection.addBlockedListener(
                    new BlockedListener() {
                        @Override
                        public void handleUnblocked() throws IOException {
                            LOGGER.info(
                                    "Connection {} is unblocked",
                                    connection.getClientProvidedName());
                        }

                        @Override
                        public void handleBlocked(String reason) throws IOException {
                            LOGGER.error(
                                    "Connection {} is blocked. reason: {}",
                                    connection.getClientProvidedName(),
                                    reason);
                        }
                    });
            return connection;
        } catch (final IOException e) {
            final String error =
                    "IO error while connecting to "
                            + Arrays.stream(addresses)
                            .map(address -> address.toString())
                            .collect(Collectors.joining(","));
            LOGGER.error(error, e);
            throw new RuntimeException(error, e);
        } catch (final TimeoutException e) {
            final String error =
                    "Timeout while connecting to "
                            + Arrays.stream(addresses)
                            .map(address -> address.toString())
                            .collect(Collectors.joining(","));
            LOGGER.error(error, e);
            throw new RuntimeException(error, e);
        }

    }

    /**
     * Returns the channel to connection pool .
     *
     * @param channel
     * @throws Exception
     */
    public synchronized void returnChannel(Channel channel) {
        if (channel == null || !channel.isOpen()) {
            channel = null; // channel is reset.
        }
        availableChannelPool.add(channel);
        LOGGER.info("Returned the borrowed channel object to the pool");
    }
}
