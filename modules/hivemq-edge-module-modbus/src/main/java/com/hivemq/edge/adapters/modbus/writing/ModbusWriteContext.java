package com.hivemq.edge.adapters.modbus.writing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.annotations.ModuleConfigField;
import com.hivemq.adapter.sdk.api.config.WriteContext;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ALL")
public class ModbusWriteContext implements WriteContext {

    @JsonProperty(value = "source", required = true)
    @ModuleConfigField(title = "Source Topic",
                       description = "The topic from which the data are received.",
                       required = true,
                       format = ModuleConfigField.FieldType.MQTT_TOPIC)
    protected @Nullable String source;

    @JsonProperty(value = "qos", required = true)
    @ModuleConfigField(title = "QoS",
                       description = "MQTT Quality of Service level",
                       required = true,
                       numberMin = 0,
                       numberMax = 2,
                       defaultValue = "0")
    protected int qos = 0;

    @JsonProperty("writingIntervalMillis")
    @ModuleConfigField(title = "Polling Interval [ms]",
                       description = "Minimum time in millisecond between consecutive writes for this mapping. " +
                               "This is intended to protect constrained devices from overloading.",
                       numberMin = 0,
                       required = true,
                       defaultValue = "1000")
    private int writingIntervalMillis = 1000; //1 second

    @JsonProperty("destination")
    @ModuleConfigField(title = "Destination Register Index",
                       description = "the register which should be written to",
                       required = true)
    private int destination;

    @JsonProperty("offset")
    @ModuleConfigField(title = "Offset in the Registers",
                       description = "The amount of registers that hold the serialized 16-bit chunks. F.e. a Short would fit into a single register (opffset=1), while a Integer needs two (offset=2).",
                       defaultValue = "1",
                       required = true)
    private int offset = 1;

    @JsonProperty("dataType")
    @ModuleConfigField(title = "Input Data Type which will be converted",
                       description = "As registers in Modbus are 16 bit, more complex data types need to be serialized before they can be written to modbus.",
                       required = true)
    private ConvertibleDataType convertibleDataType;


    @Override
    public @Nullable String getSourceMqttTopic() {
        return source;
    }

    @Override
    public int getQos() {
        return qos;
    }

    @Override
    public long getWritingInterval() {
        return writingIntervalMillis;
    }

    public int getDestination() {
        return destination;
    }

    public int getOffset() {
        return offset;
    }

    public ConvertibleDataType getConvertibleDataType() {
        return convertibleDataType;
    }
}
