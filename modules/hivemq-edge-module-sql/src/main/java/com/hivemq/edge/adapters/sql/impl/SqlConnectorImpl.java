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
package com.hivemq.edge.adapters.sql.impl;

import com.hivemq.edge.adapters.sql.ISqlClient;
import com.hivemq.edge.adapters.sql.SqlAdapterConfig;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * @author HiveMQ Adapter Generator
 */
public class SqlConnectorImpl implements ISqlClient {

    private final @NotNull SqlAdapterConfig adapterConfig;
    private Connection connection;

    public SqlConnectorImpl(final @NotNull SqlAdapterConfig adapterConfig) {
        this.adapterConfig = adapterConfig;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<Void> connect() {

            String userName = adapterConfig.getUsernamePassword() == null ? null : adapterConfig.getUsernamePassword().getUsername();
            String password = adapterConfig.getUsernamePassword() == null ? null : adapterConfig.getUsernamePassword().getPassword();
//            connection = DriverManager.getConnection(connectionUrl, userName, password);


        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> disconnect() {

        return CompletableFuture.completedFuture(null);
    }

    protected String generateConnectionUri(){
        return null;
    }
}
