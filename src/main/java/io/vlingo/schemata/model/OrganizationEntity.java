// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;

public class OrganizationEntity extends ObjectEntity<OrganizationState> implements Organization {
  private OrganizationState state;

  public OrganizationEntity() {
    this.state = new OrganizationState();
  }

  @Override
  public Completes<OrganizationState> defineWith(final OrganizationId organizationId, final String name, final String description) {
    apply(state.define(organizationId, name, description), new OrganizationDefined(organizationId, name, description), () -> state);
    return completes();
  }

  @Override
  public Completes<OrganizationState> describeAs(final String description) {
    apply(state.withDescription(description), new OrganizationDescribed(state.organizationId, description), () -> state);
    return completes();
  }

  @Override
  public Completes<OrganizationState> renameTo(final String name) {
    apply(state.withName(name), new OrganizationRenamed(state.organizationId, name), () -> state);
    return completes();
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected void persistentObject(final OrganizationState persistentObject) {
    this.state = persistentObject;
  }

  @Override
  protected Class<OrganizationState> persistentObjectType() {
    return OrganizationState.class;
  }
}
