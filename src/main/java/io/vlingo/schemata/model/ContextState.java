// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.symbio.store.object.PersistentObject;

public class ContextState extends PersistentObject {
  private static final long serialVersionUID = 1L;

  public final ContextId contextId;
  public final String description;
  public final String namespace;

  public ContextState(final ContextId contextId) {
    this(Unidentified, contextId, "", "");
  }

  public ContextState define(final String namespace, final String description) {
    return new ContextState(this.persistenceId(), this.contextId, namespace, description);
  }

  public ContextState withDescription(final String description) {
    return new ContextState(this.persistenceId(), this.contextId, this.namespace, description);
  }

  public ContextState withNamespace(final String namespace) {
    return new ContextState(this.persistenceId(), this.contextId, namespace, this.description);
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
            " contextId=" + contextId.value +
            " description=" + description +
            " namespace=" + namespace + "]";
  }

  private ContextState(final long id, final ContextId contextId, final String namespace, final String description) {
    super(id);
    this.contextId = contextId;
    this.namespace = namespace;
    this.description = description;
  }
}
