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
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitRedefined;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.query.view.UnitsView;
import io.vlingo.schemata.query.view.UnitsView.UnitItem;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;

public class UnitsProjection extends StateStoreProjectionActor<UnitsView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public UnitsProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected UnitsView currentDataFor(final Projectable projectable) {
        final UnitsView view = UnitsView.empty();
        return view;
    }

    @Override
    protected String dataIdFor(final Projectable projectable) {
        IdentifiedDomainEvent event = events.get(0);
        if (event instanceof Events.OrganizationDefined) {
            dataId = event.identity(); // organizationId
        } else {
            dataId = event.parentIdentity(); // organizationId
        }

        return dataId;
    }

    @Override
    protected boolean alwaysWrite() {
        return false;
    }

    @Override
    protected UnitsView merge(
            final UnitsView previousData,
            final int previousVersion,
            final UnitsView currentData,
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

    private UnitsView mergeEventsInto(final UnitsView initialData) {
        UnitsView mergedData = initialData;
        for (final DomainEvent event : events) {
            switch (UnitViewType.match(event)) {
                case OrganizationDefined:
                    // when an organization is defined it has an empty list of units
                    mergedData = UnitsView.empty();
                    break;
                case UnitDefined:
                    final UnitDefined defined = typed(event);
                    mergedData = mergedData.add(UnitItem.of(defined.unitId, defined.name, defined.organizationId));
                    break;
                case UnitDescribed:
                    break;
                case UnitRedefined:
                    final UnitRedefined redefined = typed(event);
                    mergedData = mergedData.replace(UnitItem.of(redefined.unitId, redefined.name, redefined.organizationId));
                    break;
                case UnitRenamed:
                    final UnitRenamed renamed = typed(event);
                    mergedData = mergedData.replace(UnitItem.of(renamed.unitId, renamed.name, renamed.organizationId));
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