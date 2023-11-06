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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.edge.modules.adapters.annotations.ModuleConfigField;
import com.hivemq.edge.modules.config.impl.AbstractPollingProtocolAdapterConfig;
import com.hivemq.edge.modules.config.impl.AbstractProtocolAdapterConfig;
import com.hivemq.edge.modules.config.impl.AdapterUsernamePassword;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.http.core.HttpConstants;

import java.util.ArrayList;
import java.util.List;

public class SqlAdapterConfig extends AbstractPollingProtocolAdapterConfig {

    public enum Driver {
        MYSQL("jdbc:mysql");

        Driver(String driver){
            this.driver = driver;
        }

        final String driver;

        public String getDriver() {
            return driver;
        }
    }

    public enum OutputFormat {
        DATATABLE, CSV, DATAPOINT
    }

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

    @JsonProperty("username-password")
    @ModuleConfigField(title = "Credentials",
                       description = "Username & Password required to access the database",
                       required = true)
    private @NotNull AdapterUsernamePassword usernamePassword;

    @JsonProperty("dbname")
    @ModuleConfigField(title = "Database Name",
                       description = "The name of the database you would like to connect to",
                       required = true)
    private @NotNull String dbName;

    @JsonProperty("sql-driver")
    @ModuleConfigField(title = "SQL Database Driver",
                       required = true,
                       description = "The type of database you would like to connect to")
    private @NotNull SqlAdapterConfig.Driver driver;

    @JsonProperty("subscriptions")
    @ModuleConfigField(title = "Subscriptions",
            description = "Map your database query result set data to MQTT Topics")
    private @NotNull List<Subscription> subscriptions = new ArrayList<>();

    public SqlAdapterConfig() {
    }

    public int getPort() {
        return port;
    }

    public @NotNull String getHost() {
        return host;
    }

    public String getDbName() {
        return dbName;
    }

    public Driver getDriver() {
        return driver;
    }

    public AdapterUsernamePassword getUsernamePassword() {
        return usernamePassword;
    }

    public @NotNull List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public static class Subscription extends AbstractProtocolAdapterConfig.Subscription {

        @JsonProperty("sql-query")
        private @NotNull String sqlQuery;

        @JsonProperty("resultset-name")
        private @NotNull String resultsetName;

        public @NotNull String getSqlQuery() {
            return sqlQuery;
        }

        public String getResultsetName() {
            return resultsetName;
        }
    }
}
