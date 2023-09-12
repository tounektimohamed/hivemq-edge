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
package com.hivemq.edge.adapters.plc4x.types.siemens;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.edge.adapters.plc4x.model.Plc4xAdapterConfig;
import com.hivemq.edge.modules.adapters.annotations.ModuleConfigField;
import com.hivemq.extension.sdk.api.annotations.NotNull;

public class Step7AdapterConfig extends Plc4xAdapterConfig {

    public enum ControllerType {
        S300,
        S400,
        S1200,
        S1500
    }

    public enum PduSize {
        b128,
        b256,
        b512,
        b1024,
        b2048,
    }

    @JsonProperty("controllerType")
    @ModuleConfigField(title = "Step 7 Controller Type",
                       description = "Http method associated with the request",
                       defaultValue = "S300")
    private @NotNull Step7AdapterConfig.ControllerType controllerType = Step7AdapterConfig.ControllerType.S300;

    @JsonProperty("pduSize")
    @ModuleConfigField(title = "Max PDU Size",
                       description = "Maximum size of a data-packet sent to and received from the remote device.",
                       defaultValue = "1024")
    private @NotNull Step7AdapterConfig.PduSize pduSize = PduSize.b1024;

    public ControllerType getControllerType() {
        return controllerType;
    }

    public PduSize getPduSize() {
        return pduSize;
    }
}
