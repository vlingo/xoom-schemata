package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.object.PersistentObject;

import java.util.concurrent.atomic.AtomicLong;

public class UnitState extends PersistentObject implements Comparable<UnitState> {
  public final UnitId unitId;
  public final String name;
  public final String description;

  private static final AtomicLong identityGenerator = new AtomicLong(0);

  public UnitState defineWith(final String name, final String description) {
    return new UnitState(this.persistenceId(), this.unitId, name, description);
  }

  public UnitState withDescription(final String description) {
    return new UnitState(this.persistenceId(), this.unitId, this.name, description);
  }

  public UnitState withName(final String name) {
    return new UnitState(this.persistenceId(), this.unitId, name, this.description);
  }

  public UnitState(final UnitId unitId) {
    this(identityGenerator.incrementAndGet(), unitId, "", "");
  }

  public UnitState(final long id, final UnitId unitId, final String name, final String description) {
    super(id);
    this.unitId = unitId;
    this.name = name;
    this.description = description;
  }

  @Override
  public int hashCode() {
    return 31 * this.unitId.value.hashCode() * this.name.hashCode() * this.description.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final UnitState otherState = (UnitState) other;

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "UnitState[persistenceId=" + persistenceId() +
            " unitId=" + unitId.value +
            " name=" + name +
            " description=" + description + "]";
  }

  @Override
  public int compareTo(final UnitState otherState) {
    return Long.compare(this.persistenceId(), otherState.persistenceId());
  }
}
