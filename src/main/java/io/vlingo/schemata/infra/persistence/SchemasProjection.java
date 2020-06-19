// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.schemata.model.Events;
import io.vlingo.schemata.query.view.SchemasView;
import io.vlingo.schemata.query.view.Tag;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;

public class SchemasProjection extends StateStoreProjectionActor<SchemasView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public SchemasProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected SchemasView currentDataFor(Projectable projectable) {
        return SchemasView.empty();
    }

    @Override
    protected String dataIdFor(Projectable projectable) {
        IdentifiedDomainEvent event = events.get(0);
        if (event instanceof Events.ContextDefined) {
            dataId = event.identity(); // contextId
        } else {
            dataId = event.parentIdentity(); // contextId
        }

        return dataId;
    }

    @Override
    protected boolean alwaysWrite() {
        return false;
    }

    @Override
    protected SchemasView merge(SchemasView previousData, int previousVersion, SchemasView currentData, int currentVersion) {
        return previousData == null
                ? mergeEventsInto(currentData)
                : mergeEventsInto(previousData);
    }

    @Override
    protected void prepareForMergeWith(final Projectable projectable) {
        events.clear();

        for (final Entry<?> entry : projectable.entries()) {
            events.add(entryAdapter().anyTypeFromEntry(entry));
        }
    }

    private SchemasView mergeEventsInto(final SchemasView initialData) {
        SchemasView mergedData = initialData;
        for (DomainEvent event : events) {
            switch (SchemaViewType.match(event)) {
                case ContextDefined:
                    // when an context is defined it has an empty list of schemas
                    mergedData = SchemasView.empty();
                    break;
                case SchemaDefined:
                    final Events.SchemaDefined defined = typed(event);
                    mergedData = mergedData.add(Tag.of(defined.schemaId, defined.name));
                    break;
                case SchemaCategorized:
                    break;
                case SchemaScoped:
                    break;
                case SchemaRenamed:
                    final Events.SchemaRenamed renamed = typed(event);
                    mergedData = mergedData.replace(Tag.of(renamed.schemaId, renamed.name));
                    break;
                case SchemaDescribed:
                    break;
                case SchemaRedefined:
                    final Events.SchemaRedefined redefined = typed(event);
                    mergedData = mergedData.replace(Tag.of(redefined.schemaId, redefined.name));
                    break;
                case Unmatched:
                    logger().warn("Event of type " + event.typeName() + " was not matched.");
                    break;
            }
        }

        logger().info("PROJECTED: " + mergedData);

        return mergedData;
    }
}
