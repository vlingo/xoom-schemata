// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Map;

import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.store.object.MapQueryExpression.FluentMap;
import io.vlingo.symbio.store.object.StateObject;

public class UnitState extends StateObject {
  private static final long serialVersionUID = 1L;

  public final UnitId unitId;
  public final String name;
  public final String description;

  public static UnitState from(final UnitId unitId) {
    return new UnitState(unitId);
  }

  public static UnitState from(final long id, final UnitId unitId, final String name, final String description) {
    return new UnitState(id, unitId, name, description);
  }

  public UnitState defineWith(final String name, final String description) {
    return new UnitState(this.persistenceId(), this.unitId, name, description);
  }

  public UnitState withDescription(final String description) {
    return new UnitState(this.persistenceId(), this.unitId, this.name, description);
  }

  public UnitState withName(final String name) {
    return new UnitState(this.persistenceId(), this.unitId, name, this.description);
  }

  @Override
  public Map<String, Object> queryMap() {
    return FluentMap
            .has("organizationId", unitId.organizationId.value)
            .and("unitId", unitId.value);
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

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "UnitState[persistenceId=" + persistenceId() +
            " unitId=" + unitId.value +
            " name=" + name +
            " description=" + description + "]";
  }

  private UnitState(final UnitId unitId) {
    this(Unidentified, unitId, "", "");
  }

  private UnitState(final long id, final UnitId unitId, final String name, final String description) {
    super(id);
    this.unitId = unitId;
    this.name = name;
    this.description = description;
  }
}
