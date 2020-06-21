// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Map;

import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.symbio.store.MapQueryExpression.FluentMap;
import io.vlingo.symbio.store.object.StateObject;

public class ContextState {
  public final ContextId contextId;
  public final String namespace;
  public final String description;

  public static ContextState from(final ContextId contextId) {
    return new ContextState(contextId);
  }

  public static ContextState from(final ContextId contextId, final String namespace, final String description) {
    return new ContextState(contextId, namespace, description);
  }

  public ContextState defineWith(final String namespace, final String description) {
    return new ContextState(this.contextId, namespace, description);
  }

  public ContextState withDescription(final String description) {
    return new ContextState(this.contextId, this.namespace, description);
  }

  public ContextState withNamespace(final String namespace) {
    return new ContextState(this.contextId, namespace, this.description);
  }

  public ContextState redefineWith(final String namespace, final String description) {
    return new ContextState(this.contextId, namespace, description);
  }

  @Override
  public int hashCode() {
    return 31 * this.contextId.value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final ContextState otherState = (ContextState) other;
    return this.contextId.equals(otherState.contextId);
  }

  @Override
  public String toString() {
    return "ContextState[contextId=" + contextId.value +
            " description=" + description +
            " namespace=" + namespace + "]";
  }

  private ContextState(final ContextId contextId) {
    this(contextId, "", "");
  }

  private ContextState(final ContextId contextId, final String namespace, final String description) {
    this.contextId = contextId;
    this.namespace = namespace;
    this.description = description;
  }
}
