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
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRedefined;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.query.view.OrganizationView;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

public class OrganizationProjection extends StateStoreProjectionActor<OrganizationView> {
  private String dataId;

  /**
   * Keep state between prepareForMergeWith(...) and merge(...) methods.
   * This list holds the latest events occurred which are not merged yet.
   */
  private final List<IdentifiedDomainEvent> events;

  public OrganizationProjection(StateStore stateStore) {
    super(stateStore);

    this.events = new ArrayList<>(2);
  }

  @Override
  protected OrganizationView currentDataFor(final Projectable projectable) {
    return OrganizationView.empty();
  }

  @Override
  protected String dataIdFor(final Projectable projectable) {
    dataId = events.get(0).identity();
    return dataId;
  }

  @Override
  protected OrganizationView merge(
          final OrganizationView previousData,
          final int previousVersion,
          final OrganizationView currentData,
          final int currentVersion) {

    final OrganizationView accumulator;
    if (previousData == null) {
      accumulator = mergeEventsInto(currentData);
    } else {
      accumulator = mergeEventsInto(previousData);
    }

    return accumulator;
  }

  @Override
  protected void prepareForMergeWith(final Projectable projectable) {
    events.clear();

    for (final Entry <?> entry : projectable.entries()) {
      events.add(entryAdapter().anyTypeFromEntry(entry));
    }
  }

  private OrganizationViewType match(final DomainEvent event) {
    try {
      return OrganizationViewType.valueOf(event.typeName());
    } catch (Exception e) {
      return OrganizationViewType.Unmatched;
    }
  }

  private OrganizationView mergeEventsInto(final OrganizationView accumulator) {
    for (final DomainEvent event : events) {
      switch (match(event)) {
      case OrganizationDefined:
        final OrganizationDefined defined = typed(event);
        accumulator.initializeWith(defined.organizationId, defined.name, defined.description);
        break;
      case OrganizationDescribed:
        final OrganizationDescribed described = typed(event);
        accumulator.mergeDescriptionWith(described.organizationId, described.description);
        break;
      case OrganizationRedefined:
        final OrganizationRedefined redefined = typed(event);
        accumulator.mergeWith(redefined.organizationId, redefined.name, redefined.description);
        break;
      case OrganizationRenamed:
        final OrganizationRenamed renamed = typed(event);
        accumulator.mergeNameWith(renamed.organizationId, renamed.name);
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
