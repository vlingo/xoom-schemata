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
import io.vlingo.schemata.model.Events.OrganizationRedefined;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.query.view.OrganizationsView;
import io.vlingo.schemata.query.view.Tag;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

public class OrganizationsProjection extends StateStoreProjectionActor<OrganizationsView> {
  private final List<IdentifiedDomainEvent> events;

  public OrganizationsProjection(StateStore stateStore) {
    super(stateStore);

    this.events = new ArrayList<>(2);
  }

  @Override
  protected OrganizationsView currentDataFor(final Projectable projectable) {
    final OrganizationsView view = new OrganizationsView();
    return view;
  }

  @Override
  protected String dataIdFor(final Projectable projectable) {
    return OrganizationsView.Id;
  }

  @Override
  protected OrganizationsView merge(
          final OrganizationsView previousData,
          final int previousVersion,
          final OrganizationsView currentData,
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

  private OrganizationViewType match(final DomainEvent event) {
    try {
      return OrganizationViewType.valueOf(event.typeName());
    } catch (Exception e) {
      return OrganizationViewType.Unmatched;
    }
  }

  private OrganizationsView mergeInto(final OrganizationsView accumulator) {
    for (final DomainEvent event : events) {
      switch (match(event)) {
      case OrganizationDefined:
        final OrganizationDefined defined = typed(event);
        accumulator.add(Tag.of(defined.organizationId, defined.name));
        break;
      case OrganizationDescribed:
        break;
      case OrganizationRedefined:
        final OrganizationRedefined redefined = typed(event);
        accumulator.replace(Tag.of(redefined.organizationId, redefined.name));
        break;
      case OrganizationRenamed:
        final OrganizationRenamed renamed = typed(event);
        accumulator.replace(Tag.of(renamed.organizationId, renamed.name));
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
