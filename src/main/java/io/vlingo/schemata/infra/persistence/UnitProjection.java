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
    final UnitView view = new UnitView(projectable.dataId());
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

    return mergeInto(currentData);
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

  private UnitView mergeInto(final UnitView accumulator) {
    for (final DomainEvent event : events) {
      switch (match(event)) {
      case UnitDefined:
        final UnitDefined defined = typed(event);
        accumulator.unitId(defined.unitId);
        accumulator.name(defined.name);
        accumulator.description(defined.description);
        break;
      case UnitDescribed:
        final UnitDescribed described = typed(event);
        accumulator.description(described.description);
        break;
      case UnitRedefined:
        final UnitRedefined redefined = typed(event);
        accumulator.name(redefined.name);
        accumulator.description(redefined.description);
        break;
      case UnitRenamed:
        final UnitRenamed renamed = typed(event);
        accumulator.name(renamed.name);
        break;
      case Unmatched:
        logger().warn("Event of type " + event.typeName() + " was not matched.");
        break;
      }
    }

    logger().info("PROJECTED: " + accumulator);

    return accumulator;
  }
}
