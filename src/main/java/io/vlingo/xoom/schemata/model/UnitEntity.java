// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;
import io.vlingo.xoom.schemata.model.Events.UnitDefined;
import io.vlingo.xoom.schemata.model.Events.UnitDescribed;
import io.vlingo.xoom.schemata.model.Events.UnitRedefined;
import io.vlingo.xoom.schemata.model.Events.UnitRenamed;
import io.vlingo.xoom.schemata.model.Id.UnitId;

public class UnitEntity extends EventSourced implements Unit {
  private UnitState state;

  public UnitEntity(final UnitId unitId) {
    super(unitId.value);
    this.state = UnitState.from(unitId);
  }

  @Override
  public Completes<UnitState> defineWith(final String name, final String description) {
    return apply(UnitDefined.with(state.unitId, name, description), () -> this.state);
  }

  @Override
  public Completes<UnitState> describeAs(final String description) {
    return apply(UnitDescribed.with(state.unitId, description), () -> this.state);
  }

  @Override
  public Completes<UnitState> redefineWith(final String name, final String description) {
    return apply(UnitRedefined.with(state.unitId, name, description), () -> this.state);
  }

  @Override
  public Completes<UnitState> renameTo(final String name) {
    return apply(UnitRenamed.with(state.unitId, name), () -> this.state);
  }

  //==============================
  // Internal implementation
  //==============================

  private void applyUnitDefined(final UnitDefined event) {
    this.state = state.defineWith(event.name, event.description);
  }

  private void applyUnitDescribed(final UnitDescribed event) {
    this.state = this.state.withDescription(event.description);
  }

  private void applyUnitRedefined(final UnitRedefined event) {
    this.state = this.state.redefineWith(event.name, event.description);
  }

  private void applyUnitRenamed(final UnitRenamed event) {
    this.state = state.withName(event.name);
  }

  static {
    registerConsumer(UnitEntity.class, UnitDefined.class, UnitEntity::applyUnitDefined);
    registerConsumer(UnitEntity.class, UnitDescribed.class, UnitEntity::applyUnitDescribed);
    registerConsumer(UnitEntity.class, UnitRedefined.class, UnitEntity::applyUnitRedefined);
    registerConsumer(UnitEntity.class, UnitRenamed.class, UnitEntity::applyUnitRenamed);
  }
}
