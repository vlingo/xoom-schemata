package io.vlingo.xoom.schemata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpecificationDiff {
  private final List<Change> changes;
  @SuppressWarnings("unused")
  private final String oldSpecification;
  @SuppressWarnings("unused")
  private final String newSpecification;

  private SpecificationDiff(String oldSpecification, String newSpecification) {
    this.changes = new ArrayList<Change>();
    this.oldSpecification = oldSpecification;
    this.newSpecification = newSpecification;
  }

  public List<Change> changes() {
    return Collections.unmodifiableList(changes);
  }

  public List<Change> compatibleChanges() {
    return Collections.unmodifiableList(changesHavingCompatibility(true));
  }

  public List<Change> incompatibleChanges() {
    return Collections.unmodifiableList(changesHavingCompatibility(false));
  }

  public boolean isCompatible() {
    return incompatibleChanges().isEmpty();
  }

  private List<Change> changesHavingCompatibility(boolean compatibility) {
    return Collections.unmodifiableList(
      changes.stream()
        .filter(c -> compatibility == c.isCompatible())
        .collect(Collectors.toList()));
  }

  public static SpecificationDiff empty() {
    return new SpecificationDiff("", "");
  }

  public static SpecificationDiff between(String oldSpecification, String newSpecification) {
    return new SpecificationDiff(oldSpecification, newSpecification);
  }

  public SpecificationDiff withChange(Change change) {
    this.changes.add(change);
    return this;
  }
}

// TODO: refactor to polymorphic impl?
class Change {

  public enum Type {
    CHANGE_FIELD,
    CHANGE_FIELD_TYPE,
    CHANGE_FIELD_DEFAULT,
    CHANGE_TYPE,
    CHANGE_FIELD_VERSION,
    ADD_FIELD,
    REMOVE_FIELD,
    MOVE_FIELD,
  }

  public final Type type;
  public final String subject;
  public final String oldValue;
  public final String newValue;

  private Change(Type type, String subject, String oldValue, String newValue) {
    this.type = type;
    this.subject = subject;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  private Change(Type type, String subject) {
    this(type, subject, null, null);
  }

  private Change(Type type, String subject, String newValue) {
    this(type, subject, null, newValue);
  }

  static Change ofType(String oldType, String newType) {
    return new Change(Type.CHANGE_TYPE, oldType, oldType, newType);
  }

  static Change ofFieldName(String oldFieldName, String newFieldName) {
    return new Change(Type.CHANGE_FIELD, oldFieldName, oldFieldName, newFieldName);
  }

  static Change ofFieldType(String fieldName, String oldType, String newType) {
    return new Change(Type.CHANGE_FIELD_TYPE, fieldName, oldType, newType);
  }

  static Change ofVersion(String fieldName, String oldVersion, String newVersion) {
    return new Change(Type.CHANGE_FIELD_VERSION, fieldName, oldVersion, newVersion);
  }

  static Change removalOfField(String fieldName) {
    return new Change(Type.REMOVE_FIELD, fieldName);
  }


  static Change additionOfField(String fieldName) {
    return new Change(Type.ADD_FIELD, fieldName);
  }

  static Change moveOf(String fieldName) {
    return new Change(Type.MOVE_FIELD, fieldName);
  }

  public boolean isCompatible() {
    return this.type == Type.ADD_FIELD;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return String.format("%s: %s  %s -> %s", type, subject, oldValue, newValue);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Change change = (Change) o;
    return type == change.type &&
      Objects.equals(subject, change.subject) &&
      Objects.equals(oldValue, change.oldValue) &&
      Objects.equals(newValue, change.newValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subject, oldValue, newValue);
  }
}
