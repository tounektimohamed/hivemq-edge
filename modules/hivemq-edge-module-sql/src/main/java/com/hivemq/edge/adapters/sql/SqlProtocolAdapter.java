/*
 * Copyright 2023-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.edge.adapters.sql;

import com.codahale.metrics.MetricRegistry;
import com.hivemq.edge.adapters.sql.impl.SqlConnectorImpl;
import com.hivemq.edge.modules.adapters.data.ProtocolAdapterDataSample;
import com.hivemq.edge.modules.adapters.impl.AbstractPollingPerSubscriptionAdapter;
import com.hivemq.edge.modules.adapters.model.ProtocolAdapterStartOutput;
import com.hivemq.edge.modules.api.adapters.ProtocolAdapterInformation;
import com.hivemq.edge.modules.config.impl.AbstractProtocolAdapterConfig;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * This is polling implementation of an adapter. The configuration object will determine the frequency of invocation.
 * Each subscription will execute in a separate job.
 * @author HiveMQ Adapter Generator
 */
public class SqlProtocolAdapter extends AbstractPollingPerSubscriptionAdapter<SqlAdapterConfig, ProtocolAdapterDataSample> {

    private static final Logger log = LoggerFactory.getLogger(SqlProtocolAdapter.class);
    private volatile @Nullable ISqlClient client;

    public SqlProtocolAdapter(@NotNull final ProtocolAdapterInformation adapterInformation,
                             @NotNull final SqlAdapterConfig adapterConfig,
                             @NotNull final MetricRegistry metricRegistry) {
        super(adapterInformation, adapterConfig, metricRegistry);
    }


    @Override
    protected CompletableFuture<ProtocolAdapterStartOutput> startInternal(final @NotNull ProtocolAdapterStartOutput output) {
        CompletableFuture<ISqlClient> startFuture =
                CompletableFuture.supplyAsync(() -> initConnection());
        startFuture.thenAccept((client) -> {
            client.connect();
            setConnectionStatus(ConnectionStatus.CONNECTED);
        }).thenRun(() -> subscribeAllInternal(client));
        return startFuture.thenApply(connection -> output);
    }

    /**
     * Called once per start operation, to contruct and initialise any client implemtation you may have
     **/
    private ISqlClient initConnection() {
        if (client == null) {
            synchronized (lock) {
                if (client == null) {
                    log.info("Creating new Instance Of Sql Connector with {}", adapterConfig);
                    client = new SqlConnectorImpl(adapterConfig);
                }
            }
        }
        return client;
    }

    protected void subscribeAllInternal(@NotNull final ISqlClient client) throws RuntimeException {
        if (adapterConfig.getSubscriptions() != null) {
            for (SqlAdapterConfig.Subscription subscription : adapterConfig.getSubscriptions()) {
                subscribeInternal(client, subscription);
            }
        }
    }


    protected void subscribeInternal(@NotNull final ISqlClient client, @NotNull final SqlAdapterConfig.Subscription subscription) {
        if (subscription != null) {
            //TODO - add some code here to subscribe some client to some external device
            startPolling(new SubscriptionSampler(this.adapterConfig, subscription));
        }
    }


    @Override
    /**
     * This method is called everytime the managed engine dictates its time to take a sample.
     * You should provide an implementation to obtain the sample either blocking on non-blocking.
     * This method is metered and long running operations may cause this job to be backed-off.
     **/
    protected CompletableFuture<ProtocolAdapterDataSample> onSamplerInvoked(
            @NotNull final SqlAdapterConfig config,
            @NotNull final AbstractProtocolAdapterConfig.Subscription subscription) {

        if(client != null && client.isConnected()){
            
            //TODO - add some code here to generate a ProtocolAdapterDataSample instance which repesents your sample to
            //publish into the MQTT engine

            byte myDatapoint = 0x2A; // initialise tag value to decimal 42
            ProtocolAdapterDataSample data = new ProtocolAdapterDataSample(myDatapoint, 
                subscription.getDestination(), 
                subscription.getQos());
            return CompletableFuture.completedFuture(data);    
        } else {
            return CompletableFuture.failedFuture(new IllegalStateException("Client <null> or not connected"));
        }
    }

    @Override
    protected CompletableFuture<Void> stopInternal() {
        if (client != null) {
            return CompletableFuture.runAsync(() -> {
                // Your runnable code here
                client.disconnect();
            });
        }
        return CompletableFuture.completedFuture(null);
    }
}
