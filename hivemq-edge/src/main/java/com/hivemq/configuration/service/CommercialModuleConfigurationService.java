package com.hivemq.configuration.service;

import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.Map;

public interface CommercialModuleConfigurationService {

    @NotNull Map<String, Object> getAllConfigs();

    void setAllConfigs(@NotNull Map<String, Object> allConfigs);
}
