// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Id.UnitId;

public interface Unit {
  static String nameFrom(final UnitId unitId) {
    return "U:"+unitId.value;
  }

  static UnitId uniqueId(final OrganizationId organizationId) {
    return UnitId.uniqueFor(organizationId);
  }

  static Completes<UnitState> with(final Stage stage, final OrganizationId organizationId, final String name, final String description) {
    return with(stage, uniqueId(organizationId), name, description);
  }

  static Completes<UnitState> with(final Stage stage, final UnitId unitId, final String name, final String description) {
    final String actorName = nameFrom(unitId);
    final Address address = stage.addressFactory().from(unitId.value, actorName);
    final Definition definition = Definition.has(UnitEntity.class, new UnitInstantiator(unitId), actorName);
    final Unit unit = stage.actorFor(Unit.class, definition, address);
    return unit.defineWith(name, description);
  }

  Completes<UnitState> defineWith(final String name, final String description);

  Completes<UnitState> describeAs(final String description);

  Completes<UnitState> redefineWith(final String name, final String description);

  Completes<UnitState> renameTo(final String name);

  static class UnitInstantiator implements ActorInstantiator<UnitEntity> {
    private static final long serialVersionUID = 8802788421322213598L;

    private final UnitId unitId;

    public UnitInstantiator(final UnitId unitId) {
      this.unitId = unitId;
    }

    @Override
    public UnitEntity instantiate() {
      return new UnitEntity(unitId);
    }

    @Override
    public Class<UnitEntity> type() {
      return UnitEntity.class;
    }
  }
}
