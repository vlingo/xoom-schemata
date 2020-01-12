// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Map;

import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.symbio.store.object.MapQueryExpression.FluentMap;
import io.vlingo.symbio.store.object.StateObject;

public class ContextState extends StateObject {
  private static final long serialVersionUID = 1L;

  public final ContextId contextId;
  public final String description;
  public final String namespace;

  public static ContextState from(final ContextId contextId) {
    return new ContextState(contextId);
  }

  public static ContextState from(final long id, final long version, final ContextId contextId, final String namespace, final String description) {
    return new ContextState(id, version, contextId, namespace, description);
  }

  public ContextState defineWith(final String namespace, final String description) {
    return new ContextState(this.persistenceId(), this.version() + 1, this.contextId, namespace, description);
  }

  public ContextState withDescription(final String description) {
    return new ContextState(this.persistenceId(), this.version() + 1, this.contextId, this.namespace, description);
  }

  public ContextState withNamespace(final String namespace) {
    return new ContextState(this.persistenceId(), this.version() + 1, this.contextId, namespace, this.description);
  }

  public ContextState redefineWith(final String namespace, final String description) {
    return new ContextState(this.persistenceId(), this.version() + 1, this.contextId, namespace, description);
  }

  @Override
  public Map<String, Object> queryMap() {
    return FluentMap
            .has("organizationId", contextId.unitId.organizationId.value)
            .and("unitId", contextId.unitId.value)
            .and("contextId", contextId.value);
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

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "ContextState[persistenceId=" + persistenceId() +
            " version=" + version() +
            " contextId=" + contextId.value +
            " description=" + description +
            " namespace=" + namespace + "]";
  }

  private ContextState(final ContextId contextId) {
    this(Unidentified, 0, contextId, "", "");
  }

  private ContextState(final long id, final long version, final ContextId contextId, final String namespace, final String description) {
    super(id, version);
    this.contextId = contextId;
    this.namespace = namespace;
    this.description = description;
  }
}
