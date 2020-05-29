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
import io.vlingo.schemata.model.Events.UnitRedefined;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.query.view.Tag;
import io.vlingo.schemata.query.view.UnitsView;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

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
        dataId = events.get(0).parentIdentity(); // organizationId
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

        final UnitsView mergedData;
        if (previousData == null) {
            mergedData = mergeInto(currentData);
        } else {
            mergedData = mergeInto(previousData);
        }

        return mergedData;
    }

    @Override
    protected void prepareForMergeWith(final Projectable projectable) {
        events.clear();

        for (final Entry <?> entry : projectable.entries()) {
            events.add(entryAdapter().anyTypeFromEntry(entry));
        }
    }

    private UnitViewType match(final DomainEvent event) {
        try {
            return UnitViewType.valueOf(event.typeName());
        } catch (Exception e) {
            return UnitViewType.Unmatched;
        }
    }

    private UnitsView mergeInto(final UnitsView initialData) {
        UnitsView mergedData = initialData;
        for (final DomainEvent event : events) {
            switch (match(event)) {
                case UnitDefined:
                    final UnitDefined defined = typed(event);
                    mergedData = mergedData.add(Tag.of(defined.unitId, defined.name));
                    break;
                case UnitDescribed:
                    break;
                case UnitRedefined:
                    final UnitRedefined redefined = typed(event);
                    mergedData = mergedData.replace(Tag.of(redefined.unitId, redefined.name));
                    break;
                case UnitRenamed:
                    final UnitRenamed renamed = typed(event);
                    mergedData = mergedData.replace(Tag.of(renamed.unitId, renamed.name));
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