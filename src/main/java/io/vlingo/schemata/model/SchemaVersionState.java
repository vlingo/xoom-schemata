package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.symbio.store.object.PersistentObject;

import java.util.concurrent.atomic.AtomicLong;

public class SchemaVersionState extends PersistentObject implements Comparable<SchemaState> {
  public final SchemaVersionId schemaVersionId;
  public final String description;
  public final SchemaVersion.Specification specification;
  public final SchemaVersion.Status status;
  public final SchemaVersion.Version versionState;

  private static final AtomicLong identityGenerator = new AtomicLong(0);

  public SchemaVersionState(final SchemaVersionId schemaVersionId) {
    this(identityGenerator.incrementAndGet(),
         schemaVersionId,
         "",
         new SchemaVersion.Specification("unknown"),
         SchemaVersion.Status.Draft,
         new SchemaVersion.Version("0.0.0"));
  }

  public SchemaVersionState(
          final long id,
          final SchemaVersionId schemaVersionId,
          final String description,
          final SchemaVersion.Specification specification,
          final SchemaVersion.Status status,
          final SchemaVersion.Version versionState) {
    super(id);
    this.schemaVersionId = schemaVersionId;
    this.description = description;
    this.specification = specification;
    this.status = status;
    this.versionState = versionState;
  }

  public SchemaVersionState asPublished() {
    return new SchemaVersionState(this.persistenceId(), this.schemaVersionId, this.description, this.specification, SchemaVersion.Status.Published, this.versionState);
  }

  public SchemaVersionState asRemoved() {
    return new SchemaVersionState(this.persistenceId(), this.schemaVersionId, this.description, this.specification, SchemaVersion.Status.Removed, this.versionState);
  }

  public SchemaVersionState defineWith(final String description, final SchemaVersion.Specification specification, final SchemaVersion.Version version) {
    return new SchemaVersionState(this.persistenceId(), this.schemaVersionId, description, specification, SchemaVersion.Status.Draft, version);
  }

  public SchemaVersionState withSpecification(final SchemaVersion.Specification specification) {
    return new SchemaVersionState(this.persistenceId(), this.schemaVersionId, this.description, specification, this.status, this.versionState);
  }

  public SchemaVersionState withDescription(final String description) {
    return new SchemaVersionState(this.persistenceId(), this.schemaVersionId, description, this.specification, this.status, this.versionState);
  }

  public SchemaVersionState withVersion(final SchemaVersion.Version version) {
    return new SchemaVersionState(this.persistenceId(), this.schemaVersionId, this.description, this.specification, this.status, version);
  }

  @Override
  public int hashCode() {
    return 31 * this.schemaVersionId.value.hashCode() * this.description.hashCode() * this.specification.hashCode() * this.status.hashCode() * this.versionState.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final SchemaVersionState otherState = (SchemaVersionState) other;

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "SchemaVersionState[persistenceId=" + persistenceId() +
            " schemaVersionId=" + schemaVersionId.value +
            " description=" + description +
            " specification=" + specification +
            " status=" + status.name() +
            " version=" + versionState + "]";
  }

  @Override
  public int compareTo(final SchemaState otherState) {
    return Long.compare(this.persistenceId(), otherState.persistenceId());
  }
}
