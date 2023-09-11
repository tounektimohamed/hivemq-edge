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
package com.hivemq.edge.adapters.plc4x.impl;

import com.hivemq.edge.adapters.plc4x.types.siemens.Step7Client;
import com.hivemq.edge.adapters.plc4x.types.siemens.Step7AdapterConfig;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HiveMQ Adapter Generator
 */
public class Plc4xConnectorImpl implements Step7Client {

    private final @NotNull Step7AdapterConfig adapterConfig;
    private final Object lock = new Object();
    private AtomicBoolean connected = new AtomicBoolean(false);

    public Plc4xConnectorImpl(final @NotNull Step7AdapterConfig adapterConfig) {
        this.adapterConfig = adapterConfig;
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public void connect() {
        connected.set(true);
    }

    @Override
    public boolean disconnect() {
        connected.set(false);
        return true;
    }
}
