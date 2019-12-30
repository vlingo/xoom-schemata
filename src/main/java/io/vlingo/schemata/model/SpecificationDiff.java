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
    return Collections.unmodifiableList(changesWithCompatibility(true));
  }

  public List<Change> incompatibleChanges() {
    return Collections.unmodifiableList(changesWithCompatibility(false));
  }

  public boolean isCompatible() {
    return incompatibleChanges().isEmpty();
  }

  private List<Change> changesWithCompatibility(boolean compatibility) {
    return Collections.unmodifiableList(
        changes.stream()
            .filter(c -> compatibility == c.compatible)
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
  // TODO: change to Change.Type (enum type, field, version ...) and from -> to value
  public final String fromType;
  public final String toType;
  public final String fromName;
  public final String toName;
  public final boolean compatible;

  private Change(String fromType, String toType, String fromName, String toName, boolean compatible) {
    this.fromType = fromType;
    this.toType = toType;
    this.fromName = fromName;
    this.toName = toName;
    this.compatible = compatible;
  }

  static Change incompatible(String fromType, String toType, String fromField, String toField) {
    return new Change(fromType, toType, fromField, toField, false);
  }

  static Change compatible(String fromType, String toType, String fromField, String toField) {
    return new Change(fromType, toType, fromField, toField, true);
  }

  static Change incompatible(String fromType, String toType) {
    return new Change(fromType, toType, null, null, false);
  }

  static Change compatible(String fromType, String toType) {
    return new Change(fromType, toType, null, null, true);
  }

  @Override
  public String toString() {
    return String.format("%s.%s -> %s.%s", fromType, fromName, toType, toName);
  }
}
