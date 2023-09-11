package com.hivemq.edge.adapters.plc4x.impl;

import com.codahale.metrics.MetricRegistry;
import com.hivemq.edge.adapters.plc4x.model.Plc4xAdapterConfig;
import com.hivemq.edge.adapters.plc4x.model.Plc4xData;
import com.hivemq.edge.adapters.plc4x.types.siemens.Step7AdapterConfig;
import com.hivemq.edge.adapters.plc4x.types.siemens.Step7Client;
import com.hivemq.edge.adapters.plc4x.types.siemens.Step7ProtocolAdapter;
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
import com.hivemq.mqtt.message.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon L Johnson
 */
public abstract class AbstractPlc4xAdapter<T extends Plc4xAdapterConfig>
        extends AbstractProtocolAdapter<T> {

    private static final Logger log = LoggerFactory.getLogger(Plc4xAdapterConfig.class);
    private final @NotNull Object lock = new Object();
    private volatile @Nullable Plc4xConnection connection;
    private @Nullable Map<Plc4xData.TYPE, Plc4xData> lastSamples = new HashMap<>();

    public AbstractPlc4xAdapter(final @NotNull ProtocolAdapterInformation adapterInformation,
                                final @NotNull T adapterConfig,
                                final @NotNull MetricRegistry metricRegistry) {
        super(adapterInformation, adapterConfig, metricRegistry);
    }

    @Override
    public CompletableFuture<Void> start(
            final @NotNull ProtocolAdapterStartInput input, final @NotNull ProtocolAdapterStartOutput output) {
        try {
            bindServices(input.moduleServices());
            initStartAttempt();
            if (client == null) {
                createConnection();
            }

            if (adapterConfig.getSubscriptions() != null) {
                for (Step7AdapterConfig.Subscription subscription : adapterConfig.getSubscriptions()) {
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

    private Step7Client createConnection() {
        if (client == null) {
            synchronized (lock) {
                if (client == null) {
                    log.info("Creating new Instance Of Plc4x Connector with {}", adapterConfig);
                    client = new Plc4xConnectorImpl(adapterConfig);
                }
            }
        }
        return client;
    }

    @Override
    public CompletableFuture<Void> stop() {
        if (client != null) {
            try {
                //-- Stop polling jobs
                protocolAdapterPollingService.getPollingJobsForAdapter(getId()).stream().forEach(
                        protocolAdapterPollingService::stopPolling);
                //-- Disconnect client
                client.disconnect();
            } catch (ProtocolAdapterException e) {
                log.error("Error disconnecting from Plc4x Client", e);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private void startPolling(final @NotNull Step7ProtocolAdapter.Poller poller) {
        protocolAdapterPollingService.schedulePolling(this, poller);
    }

    @Override
    public CompletableFuture<Void> close() {
        return stop();
    }


    protected void subscribeInternal(final @NotNull Step7AdapterConfig.Subscription subscription) {
        if (subscription != null) {
            startPolling(new Step7ProtocolAdapter.Poller(null, subscription));
        }
    }

    @Override
    public @NotNull Status status() {
        return client != null && client.isConnected() ? Status.CONNECTED : Status.DISCONNECTED;
    }

    protected void captured(final @NotNull Plc4xData data) throws ProtocolAdapterException {
        boolean publishData = true;
        if (adapterConfig.getPublishChangedDataOnly()) {
            Plc4xData previousSample = lastSamples.put(data.getType(), data);
            if (previousSample != null) {
                byte[] sample = previousSample.getData();
                publishData = !Objects.deepEquals(data.getData(), sample);
            }
        }
        if (publishData) {

            final ProtocolAdapterPublishBuilder publishBuilder = adapterPublishService.publish()
                    .withTopic(data.getTopic())
                    .withPayload(convertToJson(data.getData()))
                    .withQoS(data.getQos().getQosNumber());

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


    class Poller extends ProtocolAdapterPollingInputImpl {

        private final Plc4xData.TYPE type;
        private final Step7AdapterConfig.Subscription subscription;

        public Poller(final @NotNull Plc4xData.TYPE type, final @NotNull Step7AdapterConfig.Subscription subscription) {
            super(adapterConfig.getPublishingInterval(),
                    adapterConfig.getPublishingInterval(),
                    TimeUnit.MILLISECONDS,
                    adapterConfig.getMaxPollingErrorsBeforeRemoval());
            this.type = type;
            this.subscription = subscription;
        }

        public Plc4xData.TYPE getType() {
            return type;
        }

        @Override
        public void execute() throws Exception {
            if (!client.isConnected()) {
                client.connect();
            }
            Plc4xData data = readTag();
            if (data != null) {
                captured(data);
            }
        }

        protected Plc4xData createData() {
            Plc4xData data = new Plc4xData(type,
                    subscription.getDestination(),
                    QoS.valueOf(subscription.getQos()));
            return data;
        }

        protected Plc4xData readTag() throws ProtocolAdapterException {
            //TODO complete me
            return createData();
        }
    }
