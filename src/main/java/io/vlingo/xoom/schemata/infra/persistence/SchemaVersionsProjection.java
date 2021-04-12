// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
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
import io.vlingo.xoom.schemata.model.SchemaVersion;
import io.vlingo.xoom.schemata.query.view.SchemaVersionView;
import io.vlingo.xoom.schemata.query.view.SchemaVersionsView;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaVersionsProjection extends StateStoreProjectionActor<SchemaVersionsView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public SchemaVersionsProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected SchemaVersionsView currentDataFor(Projectable projectable) {
        return SchemaVersionsView.empty();
    }

    @Override
    protected String dataIdFor(Projectable projectable) {
        IdentifiedDomainEvent event = events.get(0);
        if (event instanceof Events.SchemaDefined) {
            dataId = event.identity(); // schema
        } else {
            dataId = event.parentIdentity(); // schema
        }

        return dataId;
    }

    @Override
    protected boolean alwaysWrite() {
        return false;
    }

    @Override
    protected SchemaVersionsView merge(SchemaVersionsView previousData, int previousVersion, SchemaVersionsView currentData, int currentVersion) {
        return previousData == null
                ? mergeEventsInto(currentData)
                : mergeEventsInto(previousData);
    }

    @Override
    protected void prepareForMergeWith(Projectable projectable) {
        events.clear();

        for (final Entry<?> entry : projectable.entries()) {
            events.add(entryAdapter().anyTypeFromEntry(entry));
        }
    }

    private SchemaVersionsView mergeEventsInto(final SchemaVersionsView initialData) {
        SchemaVersionsView mergedData = initialData;
        for (DomainEvent event : events) {
            switch (SchemaVersionViewType.match(event)) {
                case SchemaDefined:
                    // when an schema is defined it has an empty list of schemaVersions
                    mergedData = SchemaVersionsView.empty();
                    break;
                case SchemaVersionDefined:
                    final Events.SchemaVersionDefined defined = typed(event);
                    SchemaVersionView view = SchemaVersionView.with(defined.organizationId, defined.unitId, defined.contextId, defined.schemaId,
                            defined.schemaVersionId, defined.description, defined.specification, defined.status, defined.previousVersion, defined.nextVersion);
                    mergedData = mergedData.add(view);
                    break;
                case SchemaVersionDescribed:
                    final Events.SchemaVersionDescribed described = typed(event);
                    //TODO: Change this to the same method used in the other Elements (e.g.  mergedData = mergedData.add(OrganizationItem.of...)
                    mergedData = mergedData.mergeDescriptionWith(described.schemaVersionId, described.description);
                    break;
                case SchemaVersionAssigned:
                    final Events.SchemaVersionAssigned assigned = typed(event);
                    mergedData = mergedData.mergeVersionWith(assigned.schemaVersionId, assigned.version);
                    break;
                case SchemaVersionSpecified:
                    final Events.SchemaVersionSpecified specified = typed(event);
                    mergedData = mergedData.mergeSpecificationWith(specified.schemaVersionId, specified.specification);
                    break;
                case SchemaVersionPublished:
                    final Events.SchemaVersionPublished published = typed(event);
                    mergedData = mergedData.mergeStatusWith(published.schemaVersionId, SchemaVersion.Status.Published.name());
                    break;
                case SchemaVersionDeprecated:
                    final Events.SchemaVersionDeprecated deprecated = typed(event);
                    mergedData = mergedData.mergeStatusWith(deprecated.schemaVersionId, SchemaVersion.Status.Deprecated.name());
                    break;
                case SchemaVersionRemoved:
                    final Events.SchemaVersionRemoved removed = typed(event);
                    mergedData = mergedData.mergeStatusWith(removed.schemaVersionId, SchemaVersion.Status.Removed.name());
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
