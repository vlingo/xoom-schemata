// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRedefined;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.query.view.UnitView;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

public class UnitProjection extends StateStoreProjectionActor<UnitView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public UnitProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected UnitView currentDataFor(final Projectable projectable) {
        final UnitView view = UnitView.with(projectable.dataId());
        return view;
    }

    @Override
    protected String dataIdFor(final Projectable projectable) {
        final IdentifiedDomainEvent event = events.get(0);
        dataId = dataIdFrom(":", event.parentIdentity(), event.identity());
        return dataId;
    }

    @Override
    protected UnitView merge(
            final UnitView previousData,
            final int previousVersion,
            final UnitView currentData,
            final int currentVersion) {

        return previousData == null
                ? mergeEventsInto(currentData)
                : mergeEventsInto(previousData);
    }

    @Override
    protected void prepareForMergeWith(final Projectable projectable) {
        events.clear();

        for (final Entry <?> entry : projectable.entries()) {
            events.add(entryAdapter().anyTypeFromEntry(entry));
        }
    }

    private UnitView mergeEventsInto(final UnitView initialData) {
        UnitView mergedData = initialData;
        for (final DomainEvent event : events) {
            switch (UnitViewType.match(event)) {
                case UnitDefined:
                    final UnitDefined defined = typed(event);
                    mergedData = UnitView.with(defined.unitId, defined.name, defined.description);
                    break;
                case UnitDescribed:
                    final UnitDescribed described = typed(event);
                    mergedData = mergedData.mergeDescriptionWith(described.unitId, described.description);
                    break;
                case UnitRedefined:
                    final UnitRedefined redefined = typed(event);
                    mergedData = mergedData.mergeWith(redefined.unitId, redefined.name, redefined.description);
                    break;
                case UnitRenamed:
                    final UnitRenamed renamed = typed(event);
                    mergedData = mergedData.mergeNameWith(renamed.unitId, renamed.name);
                    break;
                case Unmatched:
                    logger().warn("Event of type " + event.typeName() + " was not matched.");
                    break;
                case OrganizationDefined:
                  // unused
                  break;
            }
        }

        logger().info("PROJECTED: " + mergedData);

        return mergedData;
    }
}
