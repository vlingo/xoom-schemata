// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

public class UnitView {
  public String unitId;
  public String name;
  public String description;

  public static UnitView empty() {
    return new UnitView();
  }

  public UnitView(final String unitId, final String name, final String description) {
    this.unitId = unitId;
    this.name = name;
    this.description = description;
  }

  public UnitView(final String unitId) {
    this(unitId, "", "");
  }

  public UnitView() {
    this("", "", "");
  }

  public String unitId() {
    return unitId;
  }

  public void unitId(final String unitId) {
    this.unitId = unitId;
  }

  public String name() {
    return name;
  }

  public String name(final String name) {
    return this.name = name;
  }

  public String description() {
    return description;
  }

  public String description(final String description) {
    return this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    return unitId.equals(((UnitView) other).unitId);
  }

  @Override
  public String toString() {
    return "UnitView [unitId=" + unitId + ", name=" + name + ", description=" + description + "]";
  }
}
