package io.vlingo.schemata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpecificationDiff {
  private final List<Change> changes;

  private SpecificationDiff() {
    this.changes = new ArrayList<Change>();
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
    return new SpecificationDiff();
  }

  public SpecificationDiff withChange(Change change) {
    this.changes.add(change);
    return this;
  }
}

class Change {

  public enum Type { CHANGE, ADDITION, REMOVAL }
  public enum Subject { FIELD, TYPE, VERSION }

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

  static Change ofField(String oldValue, String newValue) {
    return new Change(Type.CHANGE, Subject.FIELD, oldValue, newValue);
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

  public boolean isCompatible() {
    return this.type == Type.ADDITION;
  }

  @Override
  public String toString() {
    switch (type) {
      case CHANGE:
        return String.format("%s: %s  %s -> %s", type, subject, oldValue, newValue);
      case ADDITION:
        return String.format("%s: %s %s", type, subject, newValue);
      case REMOVAL:
        return String.format("%s: %s %s", type, subject, oldValue);
    }
    return super.toString();
  }
}
