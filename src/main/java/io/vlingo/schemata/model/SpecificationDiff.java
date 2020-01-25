package io.vlingo.schemata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpecificationDiff {
  private final List<Change> changes;
  private final String oldSpecification;
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

  public enum Type {CHANGE, ADDITION, REMOVAL, MOVE}

  public enum Subject {FIELD, TYPE, VERSION}

  public final Type type;
  public final Subject subject;
  public final String oldValue;
  public final String newValue;
  public final String oldDefault;
  public final String newDefault;

  private Change(Type type, Subject subject, String oldValue, String newValue) {
    this.type = type;
    this.subject = subject;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.oldDefault = null;
    this.newDefault = null;
  }

  private Change(Type type, Subject subject, String oldValue, String newValue, String oldDefault, String newDefault) {
    this.type = type;
    this.subject = subject;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.oldDefault = oldDefault;
    this.newDefault = newDefault;
  }

  static Change ofType(String oldValue, String newValue) {
    return new Change(Type.CHANGE, Subject.TYPE, oldValue, newValue);
  }

  static Change ofFieldName(String oldValue, String newValue) {
    return new Change(Type.CHANGE, Subject.FIELD, oldValue, newValue);
  }

  static Change ofFieldType(String oldType, String newType) {
    return new Change(Type.CHANGE, Subject.FIELD, oldType, newType);
  }

  static Change ofVersion(String oldValue, String newValue) {
    return new Change(Type.CHANGE, Subject.VERSION, oldValue, newValue);
  }

  static Change removalOfField(String value) {
    return new Change(Type.REMOVAL, Subject.FIELD, value, null);
  }


  static Change additionOfField(String value) {
    return new Change(Type.ADDITION, Subject.FIELD, null, value);
  }

  static Change moveOf(String field) {
    return new Change(Type.MOVE, Subject.FIELD, field, null);
  }

  public boolean isCompatible() {
    return this.type == Type.ADDITION;
  }

  public Type getType() { return type; }

  @Override
  public String toString() {
    switch (type) {
      case CHANGE:
        return String.format("%s: %s  %s -> %s", type, subject, oldValue, newValue);
      case ADDITION:
        return String.format("%s: %s %s", type, subject, newValue);
      case REMOVAL:
      case MOVE:
        return String.format("%s: %s %s", type, subject, oldValue);
    }
    return super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Change change = (Change) o;
    return type == change.type &&
      subject == change.subject &&
      Objects.equals(oldValue, change.oldValue) &&
      Objects.equals(newValue, change.newValue) &&
      Objects.equals(oldDefault, change.oldDefault) &&
      Objects.equals(newDefault, change.newDefault);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subject, oldValue, newValue, oldDefault, newDefault);
  }
}
