// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Map;

import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.MapQueryExpression.FluentMap;
import io.vlingo.symbio.store.object.StateObject;

public class UnitState {
  public final UnitId unitId;
  public final String name;
  public final String description;

  public static UnitState from(final UnitId unitId) {
    return new UnitState(unitId);
  }

  public static UnitState from(final UnitId unitId, final String name, final String description) {
    return new UnitState(unitId, name, description);
  }

  public UnitState defineWith(final String name, final String description) {
    return new UnitState(this.unitId, name, description);
  }

  public UnitState withDescription(final String description) {
    return new UnitState(this.unitId, this.name, description);
  }

  public UnitState withName(final String name) {
    return new UnitState(this.unitId, name, this.description);
  }

  public UnitState redefineWith(final String name, final String description) {
    return new UnitState(this.unitId, name, description);
  }

  @Override
  public int hashCode() {
    return 31 * this.unitId.value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final UnitState otherState = (UnitState) other;
    return this.unitId.equals(otherState.unitId);
  }

  @Override
  public String toString() {
    return "UnitState[unitId=" + unitId.value +
            " name=" + name +
            " description=" + description + "]";
  }

  private UnitState(final UnitId unitId) {
    this(unitId, "", "");
  }

  private UnitState(final UnitId unitId, final String name, final String description) {
    this.unitId = unitId;
    this.name = name;
    this.description = description;
  }
}
