// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;

public class ContextEntity extends EventSourced implements Context {
  static {
    BiConsumer<ContextEntity, ContextDefined> applyContextDefinedFn = ContextEntity::applyDefined;
    EventSourced.registerConsumer(ContextEntity.class, ContextDefined.class, applyContextDefinedFn);
    BiConsumer<ContextEntity, ContextDescribed> applyContextDescribedFn = ContextEntity::applyDescribed;
    EventSourced.registerConsumer(ContextEntity.class, ContextDescribed.class, applyContextDescribedFn);
    BiConsumer<ContextEntity, ContextRenamed> applyContextNamespaceChangedFn = ContextEntity::applyNamespaceChangedVlingoSchemata;
    EventSourced.registerConsumer(ContextEntity.class, ContextRenamed.class, applyContextNamespaceChangedFn);
  }

  private State state;

  public ContextEntity(final OrganizationId organizationId, final ContextId contextId, final String namespace, final String description) {
    assert(namespace != null && !namespace.isEmpty());
    assert(description != null && !description.isEmpty());
    apply(new ContextDefined(organizationId, contextId, namespace, description));
  }

  @Override
  public void changeNamespaceTo(final String namespace) {
    assert (namespace != null && !namespace.isEmpty());
    this.apply(new ContextRenamed(state.organizationId, state.contextId, namespace));
  }

  @Override
  public void describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    this.apply(new ContextDescribed(state.organizationId, state.contextId, description));
  }

  public void applyDefined(final ContextDefined e) {
    this.state = new State(OrganizationId.existing(e.organizationId), ContextId.existing(e.contextId), e.namespace, e.description);
  }

  public void applyDescribed(final ContextDescribed e) {
    this.state = this.state.withDescription(e.description);
  }

  public void applyNamespaceChangedVlingoSchemata(final ContextRenamed e) {
    state = state.withNamespace(e.namespace);
  }

  public class State {
    public final ContextId contextId;
    public final String description;
    public final String namespace;
    public final OrganizationId organizationId;

    public State withDescription(final String description) {
      return new State(this.organizationId, this.contextId, this.namespace, description);
    }

    public State withNamespace(final String namespace) {
      return new State(this.organizationId, this.contextId, namespace, this.description);
    }

    public State(final OrganizationId organizationId, final ContextId contextId, final String namespace, final String description) {
      this.organizationId = organizationId;
      this.contextId = contextId;
      this.namespace = namespace;
      this.description = description;
    }
  }
}
