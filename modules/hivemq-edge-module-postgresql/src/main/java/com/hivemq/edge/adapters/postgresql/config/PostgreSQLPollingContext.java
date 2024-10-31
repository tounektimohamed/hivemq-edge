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
package com.hivemq.edge.adapters.postgresql.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.annotations.ModuleConfigField;
import com.hivemq.adapter.sdk.api.config.MessageHandlingOptions;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.adapter.sdk.api.config.UserProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PostgreSQLPollingContext implements PollingContext {
    @JsonProperty(value = "destination", required = true)
    @ModuleConfigField(title = "Destination Topic",
            description = "The topic to publish data on",
            required = true,
            format = ModuleConfigField.FieldType.MQTT_TOPIC)
    protected @Nullable String destination;

    @JsonProperty(value = "query", required = true)
    @ModuleConfigField(title = "Query",
            description = "Query to execute on the database",
            required = true,
            format = ModuleConfigField.FieldType.UNSPECIFIED)
    protected @Nullable String query;

    @JsonProperty(value = "rowLimit", required = true)
    @ModuleConfigField(title = "Row Limit",
            description = "Number of row to retrieve (default 10, maximum 99)",
            required = true,
            numberMin = 1,
            numberMax = 99,
            defaultValue = "1")
    protected int rowLimit;

    @JsonProperty(value = "spiltLinesInIndividualMessages")
    @ModuleConfigField(title = "Split lines into individual messages ?",
            description = "Select this option to create a single message per line returned by the query (by default all lines are sent in a single message as an array).",
            defaultValue = "false")
    protected @NotNull Boolean spiltLinesInIndividualMessages;

    @JsonProperty(value = "qos", required = true)
    @ModuleConfigField(title = "QoS",
            description = "MQTT Quality of Service level",
            required = true,
            numberMin = 0,
            numberMax = 2,
            defaultValue = "1")
    protected int qos;

    @JsonProperty(value = "messageHandlingOptions")
    @ModuleConfigField(title = "Message Handling Options",
            description = "This setting defines the format of the resulting MQTT message, either a message per changed tag or a message per subscription that may include multiple data points per sample",
            enumDisplayValues = {
                    "MQTT Message Per Device Tag",
                    "MQTT Message Per Subscription (Potentially Multiple Data Points Per Sample)"},
            defaultValue = "MQTTMessagePerTag")
    protected @NotNull MessageHandlingOptions messageHandlingOptions = MessageHandlingOptions.MQTTMessagePerTag;

    @JsonProperty(value = "includeTimestamp")
    @ModuleConfigField(title = "Include Sample Timestamp In Publish?",
            description = "Include the unix timestamp of the sample time in the resulting MQTT message",
            defaultValue = "true")
    protected @NotNull Boolean includeTimestamp = Boolean.TRUE;

    @JsonProperty(value = "includeTagNames")
    @ModuleConfigField(title = "Include Tag Names In Publish?",
            description = "Include the names of the tags in the resulting MQTT publish",
            defaultValue = "false")
    protected @NotNull Boolean includeTagNames = Boolean.FALSE;

    @JsonProperty(value = "userProperties")
    @ModuleConfigField(title = "User Properties",
            description = "Arbitrary properties to associate with the subscription",
            arrayMaxItems = 10)
    private @NotNull List<UserProperty> userProperties = new ArrayList<>();

    @JsonCreator
    public PostgreSQLPollingContext(
            @JsonProperty("destination") @Nullable final String destination,
            @JsonProperty("query") @Nullable final String query,
            @JsonProperty("spiltLinesInIndividualMessages") @Nullable final String spiltLinesInIndividualMessages,
            @JsonProperty("rowLimit") final int rowLimit,
            @JsonProperty("qos") final int qos,
            @JsonProperty("userProperties") @Nullable List<UserProperty> userProperties) {
        this.destination = destination;
        this.qos = qos;
        this.query = query;
        this.rowLimit = rowLimit;
        this.spiltLinesInIndividualMessages = Boolean.valueOf(spiltLinesInIndividualMessages);

        if (userProperties != null) {
            this.userProperties = userProperties;
        }
    }

    @Override
    public @Nullable String getDestinationMqttTopic() {
        return destination;
    }


    public @Nullable String getQuery(){return query;}

    public int getRowLimit() {return rowLimit;}

    public @NotNull Boolean getSpiltLinesInIndividualMessages() {return spiltLinesInIndividualMessages;}

    @Override
    public int getQos() {
        return qos;
    }

    @Override
    public @NotNull MessageHandlingOptions getMessageHandlingOptions() {
        return messageHandlingOptions;
    }

    @Override
    public @NotNull Boolean getIncludeTimestamp() {
        return includeTimestamp;
    }

    @Override
    public @NotNull Boolean getIncludeTagNames() {
        return includeTagNames;
    }

    @Override
    public @NotNull List<UserProperty> getUserProperties() {
        return userProperties;
    }
}