package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.symbio.store.object.PersistentObject;

import java.util.concurrent.atomic.AtomicLong;

public class SchemaState extends PersistentObject implements Comparable<SchemaState> {
  public final SchemaId schemaId;
  public final Category category;
  public final String description;
  public final String name;

  private static final AtomicLong identityGenerator = new AtomicLong(0);

  public SchemaState defineWith(final Category category, final String name, final String description) {
    return new SchemaState(this.persistenceId(), this.schemaId, category, name, description);
  }

  public SchemaState withCategory(final Category category) {
    return new SchemaState(this.persistenceId(), this.schemaId, category, this.name, this.description);
  }

  public SchemaState withDescription(final String description) {
    return new SchemaState(this.persistenceId(), this.schemaId, this.category, this.name, description);
  }

  public SchemaState withName(final String name) {
    return new SchemaState(this.persistenceId(), this.schemaId, this.category, name, this.description);
  }

  public SchemaState(SchemaId schemaId) {
    this(identityGenerator.incrementAndGet(), schemaId, Category.Unknown, "", "");
  }

  public SchemaState(final long id, final SchemaId schemaId, final Category category, final String name, final String description) {
    super(id);
    this.schemaId = schemaId;
    this.category = category;
    this.name = name;
    this.description = description;
  }

  @Override
  public int hashCode() {
    return 31 * this.schemaId.value.hashCode() * this.category.hashCode() * this.description.hashCode() * this.name.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final SchemaState otherState = (SchemaState) other;

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "SchemaState[persistenceId=" + persistenceId() +
            " schemaId=" + schemaId.value +
            " category=" + category.name() +
            " description=" + description +
            " name=" + name + "]";
  }

  @Override
  public int compareTo(final SchemaState otherState) {
    return Long.compare(this.persistenceId(), otherState.persistenceId());
  }
}
