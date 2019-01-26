// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;

public class OrganizationEntity extends EventSourced implements Organization {
  private State state;

  public OrganizationEntity(final OrganizationId organizationId) {
    this.state = new State(organizationId);
  }

  @Override
  public void defineWith(final String name, final String description) {
    apply(new OrganizationDefined(state.organizationId, name, description));
  }

  @Override
  public void describeAs(final String description) {
    apply(new OrganizationDescribed(state.organizationId, description));
  }

  @Override
  public void renameTo(final String name) {
    apply(new OrganizationRenamed(state.organizationId, name));
  }

  @Override
  protected String streamName() {
    return state.organizationId.value;
  }

  public class State {
    public final OrganizationId organizationId;
    public final String name;
    public final String description;

    public State withDescription(final String description) {
      return new State(this.organizationId, this.name, description);
    }

    public State withName(final String name) {
      return new State(this.organizationId, name, this.description);
    }

    public State(OrganizationId organizationId) {
      this(organizationId, "", "");
    }

    public State(final OrganizationId organizationId, final String name, final String description) {
      this.organizationId = organizationId;
      this.name = name;
      this.description = description;
    }
  }

  static {
    BiConsumer<OrganizationEntity, OrganizationDefined> applyOrganizationDefinedFn = OrganizationEntity::applyDefined;
    EventSourced.registerConsumer(OrganizationEntity.class, OrganizationDefined.class, applyOrganizationDefinedFn);
    BiConsumer<OrganizationEntity, OrganizationDescribed> applyOrganizationDescribedFn = OrganizationEntity::applyDescribed;
    EventSourced.registerConsumer(OrganizationEntity.class, OrganizationDescribed.class, applyOrganizationDescribedFn);
    BiConsumer<OrganizationEntity, OrganizationRenamed> applyOrganizationRenamedFn = OrganizationEntity::applyRenamed;
    EventSourced.registerConsumer(OrganizationEntity.class, OrganizationRenamed.class, applyOrganizationRenamedFn);
  }

  private void applyDefined(OrganizationDefined event) {
    state = new State(OrganizationId.existing(event.organizationId), event.name, event.description);
  }

  private final void applyDescribed(OrganizationDescribed event) {
    state = state.withDescription(event.description);
  }

  private final void applyRenamed(OrganizationRenamed event) {
    state = state.withName(event.name);
  }
}
