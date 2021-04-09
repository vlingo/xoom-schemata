// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.schemata.model.Events;
import io.vlingo.xoom.schemata.query.view.ContextView;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class ContextProjection extends StateStoreProjectionActor<ContextView> {
    private String dataId;
    private final List<IdentifiedDomainEvent> events;

    public ContextProjection(StateStore stateStore) {
        super(stateStore);

        this.events = new ArrayList<>(2);
    }

    @Override
    protected ContextView currentDataFor(Projectable projectable) {
        final ContextView view = ContextView.with(projectable.dataId());
        return view;
    }

    @Override
    protected String dataIdFor(Projectable projectable) {
        final IdentifiedDomainEvent event = events.get(0);
        dataId = dataIdFrom(":", event.parentIdentity(), event.identity());
        return dataId;
    }

    @Override
    protected ContextView merge(
            ContextView previousData,
            int previousVersion,
            ContextView currentData,
            int currentVersion) {

        return previousData == null
                ? mergeInto(currentData)
                : mergeInto(previousData);
    }

    @Override
    protected void prepareForMergeWith(Projectable projectable) {
        events.clear();

        for (final Entry<?> entry : projectable.entries()) {
            events.add(entryAdapter().anyTypeFromEntry(entry));
        }
    }

    private ContextView mergeInto(final ContextView initialData) {
        ContextView mergedData = initialData;
        for (final DomainEvent event : events) {
            switch (ContextViewType.match(event)) {
                case ContextDefined:
                    final Events.ContextDefined defined = typed(event);
                    mergedData = ContextView.with(defined.contextId, defined.name, defined.description);
                    break;
                case ContextRedefined:
                    final Events.ContextRedefined redefined = typed(event);
                    mergedData = mergedData.mergeWith(redefined.contextId, redefined.name, redefined.description);
                    break;
                case ContextMovedToNamespace:
                    final Events.ContextMovedToNamespace movedToNamespace = typed(event);
                    mergedData = mergedData.mergeNamespaceWith(movedToNamespace.contextId, movedToNamespace.namespace);
                    break;
                case ContextDescribed:
                    final Events.ContextDescribed described = typed(event);
                    mergedData = mergedData.mergeDescriptionWith(described.contextId, described.description);
                    break;
                case Unmatched:
                    logger().warn("Event of type " + event.typeName() + " was not matched.");
                    break;
                case UnitDefined:
                  // unused
                  break;
            }
        }

        logger().info("PROJECTED: " + mergedData);

        return mergedData;
    }
}
