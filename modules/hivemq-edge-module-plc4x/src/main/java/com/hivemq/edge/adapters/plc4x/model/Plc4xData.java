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
package com.hivemq.edge.adapters.plc4x.model;

import com.hivemq.extension.sdk.api.annotations.NotNull;

/**
 * @author HiveMQ Adapter Generator
 */
public class Plc4xData<T extends Plc4xAdapterConfig.Subscription> {

    private final T subscription;
    private final long systemTime;
    private byte[] data;

    public Plc4xData(final @NotNull T subscription) {
        this.subscription = subscription;
        this.systemTime = System.currentTimeMillis();
    }

    public long getSystemTime() {
        return systemTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public T getSubscription() {
        return subscription;
    }
}