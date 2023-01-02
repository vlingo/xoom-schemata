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
import io.vlingo.xoom.schemata.query.view.ContextsView;
import io.vlingo.xoom.schemata.query.view.ContextsView.ContextItem;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContextsProjection extends StateStoreProjectionActor<ContextsView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public ContextsProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected ContextsView currentDataFor(Projectable projectable) {
        return ContextsView.empty();
    }

    @Override
    protected String dataIdFor(Projectable projectable) {
        IdentifiedDomainEvent event = events.get(0);
        if (event instanceof Events.UnitDefined) {
            dataId = event.identity(); // unitId
        } else {
            dataId = event.parentIdentity(); // unitId
        }

        return dataId;
    }

    @Override
    protected boolean alwaysWrite() {
        return false;
    }

    @Override
    protected ContextsView merge(
            ContextsView previousData,
            int previousVersion,
            ContextsView currentData,
            int currentVersion) {

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

    private ContextsView mergeEventsInto(final ContextsView initialData) {
        ContextsView mergedData = initialData;
        for (DomainEvent event : events) {
            switch (ContextViewType.match(event)) {
                case UnitDefined:
                    // when an unit is defined it has an empty list of contexts
                    mergedData = ContextsView.empty();
                    break;
                case ContextDefined:
                    final Events.ContextDefined defined = typed(event);
                    mergedData = mergedData.add(ContextItem.of(defined.contextId, defined.name, defined.unitId, defined.description));
                    break;
                case ContextDescribed:
                    break;
                case ContextRedefined:
                    final Events.ContextRedefined redefined = typed(event);
                    mergedData = mergedData.replace(ContextItem.of(redefined.contextId, redefined.name, redefined.unitId, redefined.description));
                    break;
                case ContextMovedToNamespace:
                    final Events.ContextMovedToNamespace movedToNamespace = typed(event);
                    mergedData = mergedData.replace(ContextItem.of(movedToNamespace.contextId, movedToNamespace.namespace));
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
