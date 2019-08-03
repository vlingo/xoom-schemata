// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.symbio.Source;

import java.util.Collections;
import java.util.List;

public class UnitEntity extends ObjectEntity<UnitState> implements Unit {
  private UnitState state;

  public UnitEntity(final UnitId unitId) {
    this.state = new UnitState(unitId);
  }

  @Override
  public Completes<UnitState> defineWith(final String name, final String description) {
    apply(this.state.defineWith(name, description), new UnitDefined(state.unitId, name, description), () -> this.state);
    return completes();
  }

  @Override
  public Completes<UnitState> describeAs(final String description) {
    apply(this.state.withDescription(description), new UnitDescribed(state.unitId, description), () -> this.state);
    return completes();
  }

  @Override
  public Completes<UnitState> renameTo(final String name) {
    apply(this.state.withName(name), new UnitRenamed(state.unitId, name), () -> this.state);
    return completes();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Tuple2<UnitState, List<Source<DomainEvent>>> whenNewState() {
    return Tuple2.from(this.state, Collections.emptyList());
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected void persistentObject(final UnitState persistentObject) {
    this.state = persistentObject;
  }

  @Override
  protected Class<UnitState> persistentObjectType() {
    return UnitState.class;
  }
}
