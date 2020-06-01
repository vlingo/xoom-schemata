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
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Events.*;
import io.vlingo.schemata.model.Scope;
import io.vlingo.schemata.query.view.SchemaView;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;

public class SchemaProjection extends StateStoreProjectionActor<SchemaView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public SchemaProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected SchemaView currentDataFor(Projectable projectable) {
        return SchemaView.with(projectable.dataId());
    }

    @Override
    protected String dataIdFor(Projectable projectable) {
        final IdentifiedDomainEvent event = events.get(0);
        dataId = dataIdFrom(":", event.parentIdentity(), event.identity());
        return dataId;
    }

    @Override
    protected SchemaView merge(SchemaView previousData, int previousVersion, SchemaView currentData, int currentVersion) {
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

    private SchemaView mergeEventsInto(final SchemaView initialData) {
        SchemaView mergedData = initialData;
        for (DomainEvent event : events) {
            switch (SchemaViewType.match(event)) {
                case SchemaDefined:
                    final SchemaDefined defined = typed(event);
                    mergedData = SchemaView.with(defined.organizationId, defined.unitId, defined.contextId, defined.schemaId,
                            Category.valueOf(defined.category), Scope.valueOf(defined.scope), defined.name, defined.description);
                    break;
                case SchemaCategorized:
                    final SchemaCategorized categorized = typed(event);
                    mergedData = mergedData.mergeCategoryWith(categorized.schemaId, Category.valueOf(categorized.category));
                    break;
                case SchemaScoped:
                    final SchemaScoped scoped = typed(event);
                    mergedData = mergedData.mergeScopeWith(scoped.schemaId, Scope.valueOf(scoped.scope));
                    break;
                case SchemaRenamed:
                    final SchemaRenamed renamed = typed(event);
                    mergedData = mergedData.mergeNameWith(renamed.schemaId, renamed.name);
                    break;
                case SchemaDescribed:
                    final SchemaDescribed described = typed(event);
                    mergedData = mergedData.mergeDescriptionWith(described.schemaId, described.description);
                    break;
                case SchemaRedefined:
                    final SchemaRedefined redefined = typed(event);
                    mergedData = mergedData.mergeWith(redefined.schemaId, Category.valueOf(redefined.category), Scope.valueOf(redefined.scope),
                            redefined.name, redefined.description);
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
