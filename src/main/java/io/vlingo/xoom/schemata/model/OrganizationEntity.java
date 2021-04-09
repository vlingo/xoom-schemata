// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;
import io.vlingo.xoom.schemata.model.Events.OrganizationDefined;
import io.vlingo.xoom.schemata.model.Events.OrganizationDescribed;
import io.vlingo.xoom.schemata.model.Events.OrganizationRedefined;
import io.vlingo.xoom.schemata.model.Events.OrganizationRenamed;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;

public class OrganizationEntity extends EventSourced implements Organization {
  private OrganizationState state;

  public OrganizationEntity(final OrganizationId organizationId) {
    super(organizationId.value);
    this.state = OrganizationState.from(organizationId);
  }

  @Override
  public Completes<OrganizationState> defineWith(final String name, final String description) {
    return apply(OrganizationDefined.with(this.state.organizationId, name, description), () -> state);
  }

  @Override
  public Completes<OrganizationState> describeAs(final String description) {
    return apply(OrganizationDescribed.with(state.organizationId, description), () -> state);
  }

  @Override
  public Completes<OrganizationState> redefineWith(final String name, final String description) {
    return apply(OrganizationRedefined.with(state.organizationId, name, description), () -> state);
  }

  @Override
  public Completes<OrganizationState> renameTo(final String name) {
    return apply(OrganizationRenamed.with(state.organizationId, name), () -> state);
  }

  //==============================
  // Internal implementation
  //==============================

  private void applyOrganizationDefined(final OrganizationDefined event) {
    this.state = state.defineWith(event.name, event.description);
  }

  private void applyOrganizationDescribed(final OrganizationDescribed event) {
    this.state = state.withDescription(event.description);
  }

  private void applyOrganizationRedefined(final OrganizationRedefined event) {
    this.state = state.redefineWith(event.name, event.description);
  }

  private void applyOrganizationRenamed(final OrganizationRenamed event) {
    this.state = state.withName(event.name);
  }

  static {
    registerConsumer(OrganizationEntity.class, OrganizationDefined.class, OrganizationEntity::applyOrganizationDefined);
    registerConsumer(OrganizationEntity.class, OrganizationDescribed.class, OrganizationEntity::applyOrganizationDescribed);
    registerConsumer(OrganizationEntity.class, OrganizationRedefined.class, OrganizationEntity::applyOrganizationRedefined);
    registerConsumer(OrganizationEntity.class, OrganizationRenamed.class, OrganizationEntity::applyOrganizationRenamed);
  }

}
