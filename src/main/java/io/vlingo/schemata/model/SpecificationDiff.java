package io.vlingo.schemata.model;

public class SpecificationDiff {
  public Boolean isCompatible;

  private SpecificationDiff(Boolean isCompatible) {
    this.isCompatible = isCompatible;
  }

  public static SpecificationDiff incompatibleDiff()  {
    return new SpecificationDiff(false);
  }

  public static SpecificationDiff compatibleDiff()  {
    return new SpecificationDiff(true);
  }

  public static SpecificationDiff of(Boolean compatible) {
    return new SpecificationDiff(compatible);
  }
}
