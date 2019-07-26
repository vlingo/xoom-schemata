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

  public ContextEntity() {
    this.state = new ContextState();
  }

  @Override
  public Completes<ContextState> defineWith(final ContextId contextId, final String name, final String description) {
    assert (name != null && !name.isEmpty());
    assert (description != null && !description.isEmpty());
    apply(state.define(contextId, name, description), ContextDefined.with(contextId, name, description), () -> state);
    return completes();
  }

  @Override
  public Completes<ContextState> changeNamespaceTo(final String namespace) {
    assert (namespace != null && !namespace.isEmpty());
    apply(state.withNamespace(namespace), ContextRenamed.with(state.contextId, namespace), () -> state);
    return completes();
  }

  @Override
  public Completes<ContextState> describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    apply(state.withDescription(description), ContextDescribed.with(state.contextId, description), () -> state);
    return completes();
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
