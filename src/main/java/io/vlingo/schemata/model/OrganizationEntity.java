// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Collections;
import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.Source;

public class OrganizationEntity extends ObjectEntity<OrganizationState> implements Organization {
  private OrganizationState state;

  public OrganizationEntity(final OrganizationId organizationId) {
    this.state = OrganizationState.from(organizationId);
  }

  public OrganizationEntity() {
    this.state = null;
  }

  @Override
  public Completes<OrganizationState> defineWith(final String name, final String description) {
    return apply(state.defineWith(name, description), new OrganizationDefined(this.state.organizationId, name, description), () -> state);
  }

  @Override
  public Completes<OrganizationState> describeAs(final String description) {
    return apply(state.withDescription(description), new OrganizationDescribed(state.organizationId, description), () -> state);
  }

  @Override
  public Completes<OrganizationState> renameTo(final String name) {
    return apply(state.withName(name), new OrganizationRenamed(state.organizationId, name), () -> state);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Tuple2<OrganizationState, List<Source<DomainEvent>>> whenNewState() {
    return state.isIdentified() ? null : Tuple2.from(state, Collections.emptyList());
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
