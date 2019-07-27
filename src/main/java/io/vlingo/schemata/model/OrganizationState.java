package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.store.object.PersistentObject;

import java.util.concurrent.atomic.AtomicLong;

public class OrganizationState extends PersistentObject implements Comparable<OrganizationState> {
  public final OrganizationId organizationId;
  public final String name;
  public final String description;

  private static final AtomicLong identityGenerator = new AtomicLong(0);

  public OrganizationState define(final OrganizationId organizationId, final String name, final String description) {
    return new OrganizationState(this.persistenceId(), organizationId, name, description);
  }

  public OrganizationState withDescription(final String description) {
    return new OrganizationState(this.persistenceId(), this.organizationId, this.name, description);
  }

  public OrganizationState withName(final String name) {
    return new OrganizationState(this.persistenceId(), this.organizationId, name, this.description);
  }

  public OrganizationState() {
    this(identityGenerator.incrementAndGet(), OrganizationId.undefined(), "", "");
  }

  public OrganizationState(final long id, final OrganizationId organizationId, final String name, final String description) {
    super(id);
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
  }

  @Override
  public int hashCode() {
    return 31 * this.organizationId.value.hashCode() * this.description.hashCode() * this.name.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final OrganizationState otherState = (OrganizationState) other;

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "OrganizationState[persistenceId=" + persistenceId() +
            " organizationId=" + organizationId.value +
            " description=" + description +
            " name=" + name + "]";
  }

  @Override
  public int compareTo(final OrganizationState otherState) {
    return Long.compare(this.persistenceId(), otherState.persistenceId());
  }
}
