// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.schemata.model.Events;
import io.vlingo.xoom.schemata.query.view.SchemasView;
import io.vlingo.xoom.schemata.query.view.SchemasView.SchemaItem;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                    mergedData = mergedData.add(SchemaItem.of(defined.schemaId, defined.name, defined.contextId, defined.category, defined.scope, defined.description));
                    break;
                case SchemaDescribed:
                    break;
                case SchemaRedefined:
                    final Events.SchemaRedefined redefined = typed(event);
                    mergedData = mergedData.replace(SchemaItem.of(redefined.schemaId, redefined.name, redefined.contextId, redefined.category, redefined.scope, redefined.description));
                    break;
                case SchemaRenamed:
                    final Events.SchemaRenamed renamed = typed(event);
                    mergedData = mergedData.replace(SchemaItem.of(renamed.schemaId, renamed.name));
                    break;
                case SchemaCategorized:
                    break;
                case SchemaScoped:
                    break;
                case Unmatched:
                    logger().warn("Event of type " + event.typeName() + " was not matched.");
                    break;
            }
        }

        logger().info("PROJECTED: " + events.stream().map(Source::typeName).collect(Collectors.toList()) + ", " + mergedData);

        return mergedData;
    }
}
