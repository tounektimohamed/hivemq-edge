package com.hivemq.edge.modules.config.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.edge.modules.adapters.annotations.ModuleConfigField;
import com.hivemq.extension.sdk.api.annotations.NotNull;

/**
 * @author Simon L Johnson
 */
public class AdapterUsernamePassword {

    @JsonProperty("username")
    @ModuleConfigField(title = "Username",
                       description = "IP Address or hostname of the device you wish to connect to")
    private @NotNull String username;

    @JsonProperty("password")
    @ModuleConfigField(title = "Password",
                       description = "IP Address or hostname of the device you wish to connect to")
    private @NotNull String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
