// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;


public class ContextEntity extends ObjectEntity<ContextState> implements Context {
  private ContextState state;

  public ContextEntity(final ContextId contextId) {
    this.state = ContextState.from(contextId);
  }

  @Override
  public Completes<ContextState> defineWith(final String namespace, final String description) {
    assert (namespace != null && !namespace.isEmpty());
    assert (description != null && !description.isEmpty());
    return apply(state.defineWith(namespace, description), ContextDefined.with(this.state.contextId, namespace, description), () -> state);
  }

  @Override
  public Completes<ContextState> describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    return apply(state.withDescription(description), ContextDescribed.with(state.contextId, description), () -> state);
  }

  @Override
  public Completes<ContextState> moveToNamespace(final String namespace) {
    assert (namespace != null && !namespace.isEmpty());
    return apply(state.withNamespace(namespace), ContextRenamed.with(state.contextId, namespace), () -> state);
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected ContextState stateObject() {
    return state;
  }

  @Override
  protected void stateObject(final ContextState stateObject) {
    this.state = stateObject;
  }

  @Override
  protected Class<ContextState> stateObjectType() {
    return ContextState.class;
  }
}
