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
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.symbio.Source;


public class ContextEntity extends ObjectEntity<ContextState> implements Context {
  private ContextState state;

  public ContextEntity(final ContextId contextId) {
    this.state = ContextState.from(contextId);
  }

  @Override
  public Completes<ContextState> defineWith(final String namespace, final String description) {
    assert (namespace != null && !namespace.isEmpty());
    assert (description != null && !description.isEmpty());
    apply(state.defineWith(namespace, description), ContextDefined.with(this.state.contextId, namespace, description), () -> state);
    return completes();
  }

  @Override
  public Completes<ContextState> describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    apply(state.withDescription(description), ContextDescribed.with(state.contextId, description), () -> state);
    return completes();
  }

  @Override
  public Completes<ContextState> moveToNamespace(final String namespace) {
    assert (namespace != null && !namespace.isEmpty());
    apply(state.withNamespace(namespace), ContextRenamed.with(state.contextId, namespace), () -> state);
    return completes();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Tuple2<ContextState, List<Source<DomainEvent>>> whenNewState() {
    return Tuple2.from(this.state, Collections.emptyList());
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected void persistentObject(final ContextState persistentObject) {
    this.state = persistentObject;
  }

  @Override
  protected Class<ContextState> persistentObjectType() {
    return ContextState.class;
  }
}
