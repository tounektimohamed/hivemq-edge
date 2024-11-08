/*
 * Copyright 2019-present HiveMQ GmbH
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
package com.hivemq.persistence.domain;

import com.hivemq.api.model.tags.DomainTagModel;
import com.hivemq.extension.sdk.api.annotations.Immutable;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Immutable
public class DomainTag {

    private final @NotNull String tagName;
    private final @NotNull String adapterId;
    private final @NotNull String protocolId;
    private final @NotNull String description;
    private final @NotNull Map<String, Object> definition;

    public DomainTag(
            final @NotNull String tagName,
            final @NotNull String adapterId,
            final @NotNull String protocolId,
            final @NotNull String description,
            final @NotNull Map<String, Object> definition) {
        this.tagName = tagName;
        this.adapterId = adapterId;
        this.protocolId = protocolId;
        this.description = description;
        this.definition = definition;
    }

    public static @NotNull DomainTag fromDomainTagEntity(
            final @NotNull DomainTagModel domainTag,
            final @NotNull String adapterId) {
        return new DomainTag(domainTag.getTag(),
                adapterId,
                domainTag.getProtocolId(),
                domainTag.getDescription(),
                domainTag.getDefinition());
    }

    public @NotNull String getTagName() {
        return tagName;
    }

    public @NotNull String getAdapterId() {
        return adapterId;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public @NotNull String getProtocolId() {
        return protocolId;
    }

    public @NotNull Map<String, Object> getDefinition() {
        return definition;
    }

    public @NotNull Map<String, Object> toTagMap() {
        return Map.of("name", tagName, "description", description, "definition", definition);
    }

    // only tag is used as duplicates based on this field are not allowed.
    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DomainTag domainTag = (DomainTag) o;
        return tagName.equals(domainTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    @Override
    public @NotNull String toString() {
        return "DomainTag{" +
                "description='" +
                description +
                '\'' +
                ", tag='" +
                tagName +
                '\'' +
                ", protocolId='" +
                protocolId +
                '\'' +
                '}';
    }
}