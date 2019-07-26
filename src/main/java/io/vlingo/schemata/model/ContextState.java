package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.symbio.store.object.PersistentObject;

import java.util.concurrent.atomic.AtomicLong;

public class ContextState extends PersistentObject implements Comparable<ContextState> {
  public final ContextId contextId;
  public final String description;
  public final String namespace;

  private static final AtomicLong identityGenerator = new AtomicLong(0);

  public ContextState() {
    this(identityGenerator.incrementAndGet(), ContextId.undefined(), "", "");
  }

  public ContextState(final long id, final ContextId contextId, final String namespace, final String description) {
    super(id);
    this.contextId = contextId;
    this.namespace = namespace;
    this.description = description;
  }

  public ContextState define(final ContextId contextId, final String namespace, final String description) {
    return new ContextState(this.persistenceId(), contextId, namespace, description);
  }

  public ContextState withDescription(final String description) {
    return new ContextState(this.persistenceId(), this.contextId, this.namespace, description);
  }

  public ContextState withNamespace(final String namespace) {
    return new ContextState(this.persistenceId(), this.contextId, namespace, this.description);
  }

  @Override
  public int hashCode() {
    return 31 * this.contextId.value.hashCode() * this.description.hashCode() * this.namespace.hashCode();
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

  @Override
  public int compareTo(final ContextState otherState) {
    return Long.compare(this.persistenceId(), otherState.persistenceId());
  }
}
