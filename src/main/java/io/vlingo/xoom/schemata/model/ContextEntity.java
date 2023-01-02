// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;
import io.vlingo.xoom.schemata.model.Events.ContextDefined;
import io.vlingo.xoom.schemata.model.Events.ContextDescribed;
import io.vlingo.xoom.schemata.model.Events.ContextMovedToNamespace;
import io.vlingo.xoom.schemata.model.Events.ContextRedefined;
import io.vlingo.xoom.schemata.model.Id.ContextId;


public class ContextEntity extends EventSourced implements Context {
  private ContextState state;

  public ContextEntity(final ContextId contextId) {
    super(contextId.value);
    this.state = ContextState.from(contextId);
  }

  @Override
  public Completes<ContextState> defineWith(final String namespace, final String description) {
    return apply(ContextDefined.with(this.state.contextId, namespace, description), () -> state);
  }

  @Override
  public Completes<ContextState> describeAs(final String description) {
    return apply(ContextDescribed.with(state.contextId, description), () -> state);
  }

  @Override
  public Completes<ContextState> moveToNamespace(final String namespace) {
    return apply(ContextMovedToNamespace.with(state.contextId, namespace), () -> state);
  }

  @Override
  public Completes<ContextState> redefineWith(final String namespace, final String description) {
    return apply(ContextRedefined.with(this.state.contextId, namespace, description), () -> state);
  }

  //==============================
  // Internal implementation
  //==============================

  private void applyContextDefined(final ContextDefined event) {
    this.state = state.defineWith(event.name, event.description);
  }

  private void applyContextDescribed(final ContextDescribed event) {
    this.state = this.state.withDescription(event.description);
  }

  private void applyContextMovedToNamespace(final ContextMovedToNamespace event) {
    this.state = state.withNamespace(event.namespace);
  }

  private void applyContextRedefined(final ContextRedefined event) {
    this.state = this.state.redefineWith(event.name, event.description);
  }

  static {
    registerConsumer(ContextEntity.class, ContextDefined.class, ContextEntity::applyContextDefined);
    registerConsumer(ContextEntity.class, ContextDescribed.class, ContextEntity::applyContextDescribed);
    registerConsumer(ContextEntity.class, ContextMovedToNamespace.class, ContextEntity::applyContextMovedToNamespace);
    registerConsumer(ContextEntity.class, ContextRedefined.class, ContextEntity::applyContextRedefined);
  }
}
