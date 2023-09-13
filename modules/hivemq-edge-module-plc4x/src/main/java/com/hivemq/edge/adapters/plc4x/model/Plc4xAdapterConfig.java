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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hivemq.edge.modules.adapters.annotations.ModuleConfigField;
import com.hivemq.edge.modules.config.impl.AbstractProtocolAdapterConfig;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Plc4xAdapterConfig extends AbstractProtocolAdapterConfig {

    @JsonProperty("port")
    @ModuleConfigField(title = "Port",
            description = "The port number on the device you wish to connect to",
            required = true,
            numberMin = PORT_MIN,
            numberMax = PORT_MAX)
    private int port;

    @JsonProperty("host")
    @ModuleConfigField(title = "Host",
            description = "IP Address or hostname of the device you wish to connect to",
            required = true,
            format = ModuleConfigField.FieldType.HOSTNAME)
    private @NotNull String host;

    @JsonProperty("publishingInterval")
    @ModuleConfigField(title = "Publishing interval [ms]",
            description = "Publishing interval in milliseconds for this subscription on the server",
            numberMin = 1,
            required = true,
            defaultValue = "1000")
    private int publishingInterval = DEFAULT_PUBLISHING_INTERVAL; //1 second

    @JsonProperty("maxPollingErrorsBeforeRemoval")
    @ModuleConfigField(title = "Max. Polling Errors",
            description = "Max. errors polling the endpoint before the polling daemon is stopped",
            numberMin = 3,
            defaultValue = "10")
    private int maxPollingErrorsBeforeRemoval = DEFAULT_MAX_POLLING_ERROR_BEFORE_REMOVAL;

//    @JsonProperty("publishChangedDataOnly")
//    @ModuleConfigField(title = "Only publish data items that have changed since last poll",
//            defaultValue = "true",
//            format = ModuleConfigField.FieldType.BOOLEAN)
//    private boolean publishChangedDataOnly = true;

    @JsonProperty("subscriptions")
    @ModuleConfigField(title = "Subscriptions",
            description = "Map your sensor data to MQTT Topics")
    private @NotNull List<Subscription> subscriptions = new ArrayList<>();

    public Plc4xAdapterConfig() {
    }

//    public boolean getPublishChangedDataOnly() {
//        return publishChangedDataOnly;
//    }

    public int getMaxPollingErrorsBeforeRemoval() {
        return maxPollingErrorsBeforeRemoval;
    }

    public int getPort() {
        return port;
    }

    public @NotNull String getHost() {
        return host;
    }

    public int getPublishingInterval() {
        return publishingInterval;
    }

    public @NotNull List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    @JsonPropertyOrder({"tagName", "tagAddress", "destination", "qos"})
    public static class Subscription extends AbstractProtocolAdapterConfig.Subscription {

        @JsonProperty(value = "tagName", required = true)
        @ModuleConfigField(title = "Tag Name",
                           description = "The name to assign to this address",
                           required = true,
                           format = ModuleConfigField.FieldType.IDENTIFIER)
        private @NotNull String tagName;

        @JsonProperty("tagAddress")
        @ModuleConfigField(title = "Tag Address",
                           description = "The well formed address of the tag to read",
                           required = true)
        private @NotNull String tagAddress;

        public @NotNull String getTagName() {
            return tagName;
        }

        public String getTagAddress() {
            return tagAddress;
        }
    }
}
