/*
 * Copyright 2019-present HiveMQ GmbH
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
package com.hivemq.edge.modules.ioc;

import com.hivemq.configuration.HivemqId;
import com.hivemq.edge.impl.events.EventServiceDelegateImpl;
import com.hivemq.edge.impl.events.InMemoryEventImpl;
import com.hivemq.edge.modules.adapters.impl.ModuleServicesImpl;
import com.hivemq.edge.modules.adapters.impl.ProtocolAdapterPollingServiceImpl;
import com.hivemq.edge.modules.adapters.impl.ProtocolAdapterPublishServiceImpl;
import com.hivemq.edge.modules.api.adapters.ModuleServices;
import com.hivemq.edge.modules.api.adapters.ProtocolAdapterPollingService;
import com.hivemq.edge.modules.api.adapters.ProtocolAdapterPublishService;
import com.hivemq.edge.modules.api.events.EventListener;
import com.hivemq.edge.modules.api.events.EventService;
import com.hivemq.edge.modules.api.events.EventStore;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.mqtt.message.QoS;
import com.hivemq.mqtt.message.publish.PUBLISH;
import com.hivemq.mqtt.message.publish.PUBLISHFactory;
import com.hivemq.mqtt.services.InternalPublishService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import static com.hivemq.edge.model.TypeIdentifier.TYPE.ADAPTER;

@Module
public abstract class ModulesModule {

    @Binds
    @Singleton
    abstract @NotNull ModuleServices moduleServices(@NotNull ModuleServicesImpl moduleServices);

    @Binds
    @Singleton
    abstract @NotNull ProtocolAdapterPublishService adapterPublishService(@NotNull ProtocolAdapterPublishServiceImpl adapterPublishService);

    @Binds
    @Singleton
    abstract @NotNull ProtocolAdapterPollingService protocolAdapterPollingService(@NotNull ProtocolAdapterPollingServiceImpl protocolAdapterPollingService);

    @Binds
    @Singleton
    abstract @NotNull EventService eventService(@NotNull EventServiceDelegateImpl eventServiceDelegate);

    @Binds
    @Singleton
    abstract @NotNull EventStore eventStore(@NotNull InMemoryEventImpl inMemoryEvent);

    @Provides
    @ElementsIntoSet
    @Singleton
    static Set<EventListener> provideEventListeners(
            final @NotNull InternalPublishService internalPublishService,
            final @NotNull HivemqId hiveMQId) {


        Set<EventListener> listeners = new HashSet<>();
        listeners.add(event -> {
            System.err.println("Event Listener got called");
            if (event.getSource().getType() != ADAPTER) {
                return;
            }

            final PUBLISH publish = new PUBLISHFactory.Mqtt5Builder().withTopic("$sys/adapter")
                    .withPayload(event.getMessage().getBytes(StandardCharsets.UTF_8))
                    .withHivemqId(hiveMQId.get())
                    .withQoS(QoS.AT_LEAST_ONCE)
                    .withOnwardQos(QoS.AT_LEAST_ONCE)
                    .withRetain(true)
                    .withMessageExpiryInterval(PUBLISH.MESSAGE_EXPIRY_INTERVAL_NOT_SET)
                    .build();

            internalPublishService.publish(publish, Executors.newScheduledThreadPool(1), "internal-system");
        });


        //TODO register event listeners here
        return listeners;
    }
}
