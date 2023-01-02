// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.EntryAdapter;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;

public class EventAdapter<T extends Source<?>> implements EntryAdapter<T, BaseEntry.TextEntry> {
    private final Class<T> type;
    private final int eventVersion;

    public EventAdapter(final Class<T> type) {
        this.type = type;
        this.eventVersion = 1;
    }

    @Override
    public T fromEntry(final BaseEntry.TextEntry entry) {
        return JsonSerialization.deserialized(entry.entryData(), type);
    }

    @Override
    public BaseEntry.TextEntry toEntry(final T object) {
        final String serialization = JsonSerialization.serialized(object);
        return new BaseEntry.TextEntry(type, eventVersion, serialization, Metadata.nullMetadata());
    }

    @Override
    public BaseEntry.TextEntry toEntry(T object, Metadata metadata) {
        final String serialization = JsonSerialization.serialized(object);
        return new BaseEntry.TextEntry(type, eventVersion, serialization, metadata);
    }

    @Override
    public BaseEntry.TextEntry toEntry(T object, int version, String id, Metadata metadata) {
        final String serialization = JsonSerialization.serialized(object);
        return new BaseEntry.TextEntry(id, type, eventVersion, serialization, version, Metadata.nullMetadata());
    }
}
