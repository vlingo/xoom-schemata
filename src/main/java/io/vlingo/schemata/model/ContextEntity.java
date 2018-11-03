// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.actors.testkit.TestState;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;

public class ContextEntity extends EventSourced implements Context {
  static {
    BiConsumer<ContextEntity, ContextDefined> applyContextDefinedFn = ContextEntity::applyDefined;
    EventSourced.registerConsumer(ContextEntity.class, ContextDefined.class, applyContextDefinedFn);
    BiConsumer<ContextEntity, ContextDescribed> applyContextDescribedFn = ContextEntity::applyDescribed;
    EventSourced.registerConsumer(ContextEntity.class, ContextDescribed.class, applyContextDescribedFn);
    BiConsumer<ContextEntity, ContextRenamed> applyContextNamespaceChangedFn = ContextEntity::applyNamespaceChanged;
    EventSourced.registerConsumer(ContextEntity.class, ContextRenamed.class, applyContextNamespaceChangedFn);
  }

  private State state;

  public ContextEntity(final ContextId contextId, final String namespace, final String description) {
    assert (namespace != null && !namespace.isEmpty());
    assert (description != null && !description.isEmpty());
    apply(ContextDefined.with(contextId, namespace, description));
  }

  @Override
  public void changeNamespaceTo(final String namespace) {
    assert (namespace != null && !namespace.isEmpty());
    this.apply(ContextRenamed.with(state.contextId, namespace));
  }

  @Override
  public void describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    this.apply(ContextDescribed.with(state.contextId, description));
  }

  private void applyDefined(final ContextDefined e) {
    this.state = new State(ContextId.existing(e.contextId), e.namespace, e.description);
  }

  private void applyDescribed(final ContextDescribed e) {
    this.state = this.state.withDescription(e.description);
  }

  private void applyNamespaceChanged(final ContextRenamed e) {
    state = state.withNamespace(e.namespace);
  }

  public class State {
    public final ContextId contextId;
    public final String description;
    public final String namespace;

    public State(final ContextId contextId, final String namespace, final String description) {
      this.contextId = contextId;
      this.namespace = namespace;
      this.description = description;
    }

    public State withDescription(final String description) {
      return new State(this.contextId, this.namespace, description);
    }

    public State withNamespace(final String namespace) {
      return new State(this.contextId, namespace, this.description);
    }
  }

  @Override
  public TestState viewTestState() {
    TestState testState = new TestState();
    testState.putValue("sourced", this);
    return testState;
  }
}
