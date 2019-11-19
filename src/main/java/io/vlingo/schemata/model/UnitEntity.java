// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRedefined;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.model.Id.UnitId;

public class UnitEntity extends ObjectEntity<UnitState> implements Unit {
  private UnitState state;

  public UnitEntity(final UnitId unitId) {
    this.state = UnitState.from(unitId);
  }

  @Override
  public Completes<UnitState> defineWith(final String name, final String description) {
    return apply(this.state.defineWith(name, description), new UnitDefined(state.unitId, name, description), () -> this.state);
  }

  @Override
  public Completes<UnitState> describeAs(final String description) {
    return apply(this.state.withDescription(description), new UnitDescribed(state.unitId, description), () -> this.state);
  }

  @Override
  public Completes<UnitState> redefineWith(final String name, final String description) {
    return apply(this.state.redefineWith(name, description), new UnitRedefined(state.unitId, name, description), () -> this.state);
  }

  @Override
  public Completes<UnitState> renameTo(final String name) {
    return apply(this.state.withName(name), new UnitRenamed(state.unitId, name), () -> this.state);
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected UnitState stateObject() {
    return state;
  }

  @Override
  protected void stateObject(final UnitState stateObject) {
    this.state = stateObject;
  }

  @Override
  protected Class<UnitState> stateObjectType() {
    return UnitState.class;
  }
}
