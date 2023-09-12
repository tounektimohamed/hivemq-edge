package com.hivemq.edge.adapters.plc4x.impl;

import com.codahale.metrics.MetricRegistry;
import com.hivemq.api.model.events.Event;
import com.hivemq.edge.adapters.plc4x.Plc4xException;
import com.hivemq.edge.adapters.plc4x.model.Plc4xAdapterConfig;
import com.hivemq.edge.adapters.plc4x.model.Plc4xData;
import com.hivemq.edge.adapters.plc4x.types.siemens.Step7AdapterConfig;
import com.hivemq.edge.modules.adapters.ProtocolAdapterException;
import com.hivemq.edge.modules.adapters.impl.AbstractProtocolAdapter;
import com.hivemq.edge.modules.adapters.params.ProtocolAdapterStartInput;
import com.hivemq.edge.modules.adapters.params.ProtocolAdapterStartOutput;
import com.hivemq.edge.modules.adapters.params.impl.ProtocolAdapterPollingInputImpl;
import com.hivemq.edge.modules.api.adapters.ProtocolAdapterInformation;
import com.hivemq.edge.modules.api.adapters.ProtocolAdapterPublishBuilder;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.mqtt.handler.publish.PublishReturnCode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcResponse;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Simon L Johnson
 */
public abstract class AbstractPlc4xAdapter<T extends Plc4xAdapterConfig>
        extends AbstractProtocolAdapter<T> {

    private static final Logger log = LoggerFactory.getLogger(Plc4xAdapterConfig.class);
    private static final @NotNull PlcDriverManager driverManager = new PlcDriverManager();
    private final @NotNull Object lock = new Object();
    private volatile @Nullable Plc4xConnection connection;

    public enum ReadType {
        Read,
        Subscribe
    }

    public AbstractPlc4xAdapter(
            final @NotNull ProtocolAdapterInformation adapterInformation, final @NotNull T adapterConfig, final @NotNull MetricRegistry metricRegistry) {
        super(adapterInformation, adapterConfig, metricRegistry);
    }

    @Override
    public CompletableFuture<Void> start(
            final @NotNull ProtocolAdapterStartInput input, final @NotNull ProtocolAdapterStartOutput output) {
        try {
            bindServices(input.moduleServices());
            initStartAttempt();
            if (connection == null) {
                createConnection();
            }
            if (adapterConfig.getSubscriptions() != null) {
                for (T.Subscription subscription : adapterConfig.getSubscriptions()) {
                    subscribeInternal(subscription);
                }
            }
            output.startedSuccessfully("Successfully connected");
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            output.failStart(e, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private Plc4xConnection createConnection() throws Plc4xException {
        if (connection == null) {
            synchronized (lock) {
                if (connection == null) {
                    log.info("Creating new Instance Of Plc4x Connector with {}", adapterConfig);
                    connection = new Plc4xConnection(driverManager, adapterConfig, true) {
                        @Override
                        protected String getProtocol() {
                            return getProtocolHandler();
                        }
                    };
                }
            }
        }
        return connection;
    }

    @Override
    public CompletableFuture<Void> stop() {
        if (connection != null) {
            try {
                //-- Stop polling jobs
                protocolAdapterPollingService.getPollingJobsForAdapter(getId()).stream().forEach(protocolAdapterPollingService::stopPolling);
                //-- Disconnect client
                connection.disconnect();
            } catch (Exception e) {
                log.error("Error disconnecting from Plc4x Client", e);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private void startPolling(final @NotNull Poller poller) {
        protocolAdapterPollingService.schedulePolling(this, poller);
    }

    @Override
    public CompletableFuture<Void> close() {
        return stop();
    }

    protected void subscribeInternal(final @NotNull T.Subscription subscription) throws Plc4xException {
        if (subscription != null) {
            switch(getReadType()) {
                case Subscribe:
                    if(log.isDebugEnabled()){
                        log.debug("Subscribing to tag [{}] on connection", subscription.getTagName());
                    }
                    connection.subscribe(subscription,
                            plcSubscriptionEvent ->
                                    processSubscriptionResponse(subscription, plcSubscriptionEvent));
                    break;
                case Read:
                    if(log.isDebugEnabled()){
                        log.debug("Scheduling read of tag [{}] on connection", subscription.getTagName());
                    }
                    startPolling(new Poller(subscription));
                    break;
            }
        }
    }

    @Override
    public @NotNull Status status() {
        return connection != null && connection.isConnected() ? Status.CONNECTED : Status.DISCONNECTED;
    }

    protected void captured(final @NotNull Plc4xData data) throws ProtocolAdapterException {
        boolean publishData = true;
        if (publishData) {

            final ProtocolAdapterPublishBuilder publishBuilder = adapterPublishService.publish()
                    .withTopic(data.getSubscription().getDestination())
                    .withPayload(convertToJson(data.getData()))
                    .withQoS(data.getSubscription().getQos());
            final CompletableFuture<PublishReturnCode> publishFuture = publishBuilder.send();
            publishFuture.thenAccept(publishReturnCode -> {
                protocolAdapterMetricsHelper.incrementReadPublishSuccess();
            }).exceptionally(throwable -> {
                protocolAdapterMetricsHelper.incrementReadPublishFailure();
                log.warn("Error Publishing Plc4x Payload", throwable);
                return null;
            });
        }
    }

    protected void processSubscriptionResponse(final @NotNull T.Subscription subscription,
                                               final @NotNull PlcSubscriptionEvent subscriptionEvent){

    }

    protected void processReadResponse(final @NotNull T.Subscription subscription,
                                       final @NotNull PlcReadResponse readEvent){
        processPlcFieldData(subscription, Plc4xDataUtils.readDataFromReadResponse(readEvent));
    }

    protected void processPlcFieldData(final @NotNull T.Subscription subscription, final @NotNull List<Pair<String, byte[]>> l){
        for (Pair<String, byte[]> p : l) {
            if (p.getRight() != null && p.getRight().length > 0) {
                try {
                    if(log.isDebugEnabled()){
                        log.info("Received field {} from plc4x-connection -> {}", p.getLeft(), Plc4xDataUtils.toHex(p.getRight()));
                    }
                    if(p.getValue() != null && p.getValue().length > 0){
                        Plc4xData data = new Plc4xData(subscription);
                        data.setData(p.getValue());
                        captured(data);
                    }
                } catch (Exception e) {
                    if(log.isWarnEnabled()){
                        log.warn("Error receiving bytes from plc4x-connection -> field {}", p.getLeft(), e);
                    }
                }
            }
        }
    }
    class Poller extends ProtocolAdapterPollingInputImpl {
        private final T.Subscription subscription;

        public Poller(final @NotNull T.Subscription subscription) {
            super(adapterConfig.getPublishingInterval(),
                    adapterConfig.getPublishingInterval(),
                    TimeUnit.MILLISECONDS,
                    adapterConfig.getMaxPollingErrorsBeforeRemoval());
            this.subscription = subscription;
        }

        @Override
        public void execute() throws Exception {
            if (connection.isConnected()) {
                connection.read(subscription,
                        plcResponse ->
                                AbstractPlc4xAdapter.this.processReadResponse(subscription, plcResponse));
            }
        }
    }

    /**
     * The protocol Handler is the prefix of the JNDI Connection URI used to instantiate the connection from the factory
     * @return the prefix to use, for example "opcua"
     */
    protected abstract String getProtocolHandler();

    /**
     * Whether to use read or subscription types
     * @return Decides on the mode of reading data from the underlying connection
     */
    protected abstract ReadType getReadType();
}
